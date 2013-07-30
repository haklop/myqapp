package com.infoq.myqapp.controller;

import com.infoq.myqapp.AuthenticationFilter;
import com.infoq.myqapp.domain.FeedEntry;
import com.infoq.myqapp.domain.UserProfile;
import com.infoq.myqapp.service.TrelloAuthenticationService;
import com.infoq.myqapp.service.TrelloService;
import com.infoq.myqapp.service.UserService;
import com.infoq.myqapp.service.exception.CardConflictException;
import com.julienvey.trello.domain.Member;
import com.julienvey.trello.domain.TList;
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

        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_TRELLO_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);

        try {
            trelloService.addCardToTrello(feed, accessToken);
        } catch (CardConflictException e) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/lists")
    public ResponseEntity<List<TList>> getLists(WebRequest request) {
        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_TRELLO_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);

        return new ResponseEntity<>(trelloService.getLists(accessToken), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list/{listId}")
    @ResponseBody
    public ResponseEntity<TList> getListById(@PathVariable String listId, WebRequest request) {
        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_TRELLO_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);

        return new ResponseEntity<>(trelloService.getList(accessToken, listId), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public String login(WebRequest request, HttpServletRequest httpServletRequest) {
        String email = (String) request.getAttribute(AuthenticationFilter.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION);
        logger.info("Trying to retrieve a token for {}", email);

        UserProfile userProfile = userService.get(email);
        if (userProfile == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        OAuthService service = trelloAuthenticationService.getService(httpServletRequest.getRequestURL()
                .toString().replace("/api/trello/login", "/api/trello/callback"));

        Token requestToken = service.getRequestToken();
        request.setAttribute(AuthenticationFilter.ATTR_TRELLO_OAUTH_REQUEST_TOKEN, requestToken, RequestAttributes.SCOPE_SESSION);

        // redirect to trello auth page
        return "redirect:" + service.getAuthorizationUrl(requestToken);
    }

    @RequestMapping(value = {"/callback"}, method = RequestMethod.GET)
    public String callback(@RequestParam(value = "oauth_verifier", required = false) String oauthVerifier, WebRequest request) {
        String email = (String) request.getAttribute(AuthenticationFilter.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION);
        UserProfile userProfile = userService.get(email);
        if (userProfile == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        OAuthService service = trelloAuthenticationService.getService();
        Token requestToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_TRELLO_OAUTH_REQUEST_TOKEN, RequestAttributes.SCOPE_SESSION);

        Verifier verifier = new Verifier(oauthVerifier);
        Token accessToken = service.getAccessToken(requestToken, verifier);
        logger.info("Access Granted to Trello for {} with token {}", email, accessToken);

        request.setAttribute(AuthenticationFilter.ATTR_TRELLO_OAUTH_ACCESS_TOKEN, accessToken, RequestAttributes.SCOPE_SESSION);

        userProfile.setTokenTrello(accessToken);
        userService.save(userProfile);

        return "redirect:/";
    }

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public ResponseEntity getUserInfo(WebRequest request) {
        //this check is here to verify token validity accross user navigation on pages
        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_TRELLO_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);
        if (accessToken == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        try {
            trelloService.getUserInfo(accessToken);
            UserProfile profile = userService.get((String) request.getAttribute(AuthenticationFilter.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION));
            return new ResponseEntity<>(profile, HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
            throw e;
        }
    }

    @RequestMapping(value = "/member", method = RequestMethod.GET)
    public ResponseEntity getMembers(WebRequest request) {
        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_TRELLO_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);

        List<Member> members = trelloService.getMembers(accessToken);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity handleClientException(HttpClientErrorException e) {
        return new ResponseEntity(e.getStatusCode());
    }
}
