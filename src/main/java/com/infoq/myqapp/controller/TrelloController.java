package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.ErrorMessage;
import com.infoq.myqapp.domain.FeedEntry;
import com.infoq.myqapp.domain.UserProfile;
import com.infoq.myqapp.domain.ValidatedContent;
import com.infoq.myqapp.service.TrelloAuthenticationService;
import com.infoq.myqapp.service.TrelloService;
import com.infoq.myqapp.service.UserService;
import com.infoq.myqapp.service.exception.CardConflictException;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.exception.TrelloHttpException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/trello")
@Secured("ROLE_EDITOR")
public class TrelloController {

    private static final Logger logger = LoggerFactory.getLogger(TrelloController.class);
    public static final String ATTR_TRELLO_OAUTH_REQUEST_TOKEN = "oauthrequestoken";

    @Resource
    private TrelloAuthenticationService trelloAuthenticationService;

    @Resource
    private TrelloService trelloService;

    @Resource
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST, value = "/card")
    public ResponseEntity addToTrello(@RequestBody @Valid FeedEntry feed, WebRequest request) {
        logger.info("Adding card to Trello {}", feed.getTitle());

        Token accessToken = getToken(request);
        if (accessToken == null || accessToken.isEmpty()) {
            return new ResponseEntity<>(new ErrorMessage(HttpStatus.FORBIDDEN.value(), "trelloToken", "Trello token is missing"),
                    HttpStatus.FORBIDDEN);
        }

        try {
            trelloService.addCardToTrello(feed, accessToken);
        } catch (CardConflictException e) {
            return new ResponseEntity<>(new ErrorMessage(HttpStatus.CONFLICT.value(), "trello", "Card already inserted"),
                    HttpStatus.CONFLICT);
        } catch (HttpClientErrorException e) {
            return catchClientException(e);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/validated")
    @ResponseBody
    public ResponseEntity getValidatedContent(WebRequest request) {
        Token accessToken = getToken(request);
        if (accessToken == null || accessToken.isEmpty()) {
            return new ResponseEntity<>(new ErrorMessage(HttpStatus.FORBIDDEN.value(), "trelloToken", "Trello token is missing"),
                    HttpStatus.FORBIDDEN);
        }

        try {
            TList list = trelloService.getValidatedList(accessToken);
            List<ValidatedContent> validatedContents = trelloService.enhancedValidatedContentList(list);
            return new ResponseEntity<>(validatedContents, HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            return catchClientException(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public String login(WebRequest request, HttpServletRequest httpServletRequest) {
        String email = (String) request.getAttribute(GoogleController.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION);
        logger.info("Trying to retrieve a token for {}", email);

        UserProfile userProfile = userService.get(email);
        if (userProfile == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        OAuthService service = trelloAuthenticationService.getService(httpServletRequest.getRequestURL()
                .toString().replace("/api/trello/login", "/api/trello/callback"));

        Token requestToken = service.getRequestToken();
        request.setAttribute(ATTR_TRELLO_OAUTH_REQUEST_TOKEN, requestToken, RequestAttributes.SCOPE_SESSION);

        // redirect to trello auth page
        return "redirect:" + service.getAuthorizationUrl(requestToken);
    }

    @RequestMapping(value = {"/callback"}, method = RequestMethod.GET)
    public String callback(@RequestParam(value = "oauth_verifier", required = false) String oauthVerifier, WebRequest request) {
        String email = (String) request.getAttribute(GoogleController.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION);
        UserProfile userProfile = userService.get(email);
        if (userProfile == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        OAuthService service = trelloAuthenticationService.getService();
        Token requestToken = (Token) request.getAttribute(ATTR_TRELLO_OAUTH_REQUEST_TOKEN, RequestAttributes.SCOPE_SESSION);

        Verifier verifier = new Verifier(oauthVerifier);
        Token accessToken = service.getAccessToken(requestToken, verifier);
        logger.info("Access Granted to Trello for {} with token {}", email, accessToken);

        userProfile.setTokenTrello(accessToken);
        userService.save(userProfile);

        return "redirect:/";
    }

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public ResponseEntity getUserInfo(WebRequest request) { // TODO remove me ?
        UserProfile profile = userService.get((String) request.getAttribute(GoogleController.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION));
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @RequestMapping(value = "/member", method = RequestMethod.GET)
    public ResponseEntity getMembers(WebRequest request) {
        Token accessToken = getToken(request);
        if (accessToken == null || accessToken.isEmpty()) {
            return new ResponseEntity<>(new ErrorMessage(HttpStatus.FORBIDDEN.value(), "trelloToken", "Trello token is missing"),
                    HttpStatus.FORBIDDEN);
        }

        try {
            List<Member> members = trelloService.getMembers(accessToken);
            return new ResponseEntity<>(members, HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            return catchClientException(e);
        }

    }

    private Token getToken(WebRequest request) {
        String email = (String) request.getAttribute(GoogleController.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION);
        UserProfile userProfile = userService.get(email);
        return userProfile.getTokenTrello();
    }

    private ResponseEntity catchClientException(HttpClientErrorException e) {
        if (e.getStatusCode().value() == 401) {
            return new ResponseEntity<>(new ErrorMessage(HttpStatus.FORBIDDEN.value(), "trelloToken", "Trello token is expired"),
                    HttpStatus.FORBIDDEN);
        } else {
            throw e;
        }
    }

    @ExceptionHandler(TrelloHttpException.class)
    public ResponseEntity handleTrelloException(TrelloHttpException e) {
        if (e.getCause() != null && e.getCause() instanceof HttpClientErrorException) {
            HttpClientErrorException exception = (HttpClientErrorException) e.getCause();
            return catchClientException(exception);
        }
        logger.warn("Unknown Trello Exception", e);
        return new ResponseEntity<>(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "trello", "Something looks wrong with Trello"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
