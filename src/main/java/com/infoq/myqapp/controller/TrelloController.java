package com.infoq.myqapp.controller;

import com.infoq.myqapp.AuthenticationFilter;
import com.infoq.myqapp.domain.FeedEntry;
import com.infoq.myqapp.domain.UserProfile;
import com.infoq.myqapp.repository.UserProfileRepository;
import com.infoq.myqapp.service.TrelloAuthenticationService;
import com.infoq.myqapp.service.TrelloService;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/trello")
public class TrelloController {

    private static final Logger LOG = LoggerFactory.getLogger(TrelloController.class);

    @Resource
    private TrelloAuthenticationService trelloAuthenticationService;

    @Resource
    private TrelloService trelloService;

    @Resource
    private UserProfileRepository userProfileRepository;

    @RequestMapping(method = RequestMethod.POST, value = "/card")
    public ResponseEntity addToTrello(@RequestBody FeedEntry feed, WebRequest request) {
        LOG.info("Adding card to Trello {}", feed.getTitle());

        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);

        try {
            trelloService.addCardToTrello(feed, accessToken);
        } catch (CardConflictException e) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/lists")
    public ResponseEntity<List<TList>> getLists(WebRequest request) {
        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);

        return new ResponseEntity<>(trelloService.getLists(accessToken), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public String login(WebRequest request, HttpServletRequest httpServletRequest) {
        String email = (String) request.getAttribute(AuthenticationFilter.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION);
        LOG.info("Trying to retrieve a token for {}", email);

        UserProfile userProfile = userProfileRepository.findOne(email);
        if (userProfile == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        OAuthService service = trelloAuthenticationService.getService(httpServletRequest.getRequestURL()
                .toString().replace("/api/trello/login", "/api/trello/callback"));

        Token requestToken = service.getRequestToken();
        request.setAttribute(AuthenticationFilter.ATTR_OAUTH_REQUEST_TOKEN, requestToken, RequestAttributes.SCOPE_SESSION);

        // redirect to trello auth page
        return "redirect:" + service.getAuthorizationUrl(requestToken);
    }

    @RequestMapping(value = {"/callback"}, method = RequestMethod.GET)
    public String callback(@RequestParam(value = "oauth_verifier", required = false) String oauthVerifier, WebRequest request) {
        String email = (String) request.getAttribute(AuthenticationFilter.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION);
        UserProfile userProfile = userProfileRepository.findOne(email);
        if (userProfile == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        OAuthService service = trelloAuthenticationService.getService();
        Token requestToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_OAUTH_REQUEST_TOKEN, RequestAttributes.SCOPE_SESSION);

        Verifier verifier = new Verifier(oauthVerifier);
        Token accessToken = service.getAccessToken(requestToken, verifier);
        LOG.info("Access Granted to Trello for {} with token {}", email, accessToken);

        request.setAttribute(AuthenticationFilter.ATTR_OAUTH_ACCESS_TOKEN, accessToken, RequestAttributes.SCOPE_SESSION);

        userProfile.setTokenTrello(accessToken);
        userProfileRepository.save(userProfile);

        return "redirect:/";
    }

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public ResponseEntity getUserInfo(WebRequest request) {
        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);
        if (accessToken == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Member member = trelloService.getUserInfo(accessToken);
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @RequestMapping(value = "/member", method = RequestMethod.GET)
    public ResponseEntity getMembers(WebRequest request) {
        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);

        List<Member> members = trelloService.getMembers(accessToken);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity handleClientException(HttpClientErrorException e) {
        return new ResponseEntity(e.getStatusCode());
    }
}
