package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.ErrorMessage;
import com.infoq.myqapp.domain.FeedEntry;
import com.infoq.myqapp.domain.UserProfile;
import com.infoq.myqapp.service.TrelloAuthenticationService;
import com.infoq.myqapp.service.TrelloService;
import com.infoq.myqapp.service.UserService;
import com.infoq.myqapp.service.exception.CardConflictException;
import com.julienvey.trello.domain.Member;
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
    @Secured("ROLE_EDITOR")
    public ResponseEntity addToTrello(@RequestBody @Valid FeedEntry feed, WebRequest request) {
        logger.info("Adding card to Trello {}", feed.getTitle());

        Token accessToken = getToken(request);
        if (accessToken == null || accessToken.isEmpty()) {
            return new ResponseEntity<>(new ErrorMessage(HttpStatus.FORBIDDEN.value(), "trelloToken", "Trello token is missing"),
                    HttpStatus.FORBIDDEN);
        }

        try {
            trelloService.addCardToTrello(feed, accessToken); // TODO catch token error
        } catch (CardConflictException e) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/lists")
    @Secured("ROLE_EDITOR")
    public ResponseEntity getLists(WebRequest request) {
        Token accessToken = getToken(request);
        if (accessToken == null || accessToken.isEmpty()) {
            return new ResponseEntity<>(new ErrorMessage(HttpStatus.FORBIDDEN.value(), "trelloToken", "Trello token is missing"),
                    HttpStatus.FORBIDDEN);
        }

        // TODO catch token error
        return new ResponseEntity<>(trelloService.getLists(accessToken), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list/{listId}")
    @ResponseBody
    @Secured("ROLE_EDITOR")
    public ResponseEntity getListById(@PathVariable String listId, WebRequest request) {
        Token accessToken = getToken(request);
        if (accessToken == null || accessToken.isEmpty()) {
            return new ResponseEntity<>(new ErrorMessage(HttpStatus.FORBIDDEN.value(), "trelloToken", "Trello token is missing"),
                    HttpStatus.FORBIDDEN);
        }

        // TODO catch token error
        return new ResponseEntity<>(trelloService.getList(accessToken, listId), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    @Secured("ROLE_ANONYMOUS")
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
    @Secured("ROLE_ANONYMOUS")
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
    @Secured("ROLE_EDITOR")
    public ResponseEntity getUserInfo(WebRequest request) { // TODO remove me ?
        UserProfile profile = userService.get((String) request.getAttribute(GoogleController.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION));
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @RequestMapping(value = "/member", method = RequestMethod.GET)
    @Secured("ROLE_EDITOR")
    public ResponseEntity getMembers(WebRequest request) {
        Token accessToken = getToken(request);
        if (accessToken == null || accessToken.isEmpty()) {
            return new ResponseEntity<>(new ErrorMessage(HttpStatus.FORBIDDEN.value(), "trelloToken", "Trello token is missing"),
                    HttpStatus.FORBIDDEN);
        }

        List<Member> members = trelloService.getMembers(accessToken); // TODO catch token error
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity handleClientException(HttpClientErrorException e) {
        return new ResponseEntity(e.getStatusCode());
    }

    private Token getToken(WebRequest request) {
        String email = (String) request.getAttribute(GoogleController.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION);
        UserProfile userProfile = userService.get(email);
        return userProfile.getTokenTrello();
    }
}
