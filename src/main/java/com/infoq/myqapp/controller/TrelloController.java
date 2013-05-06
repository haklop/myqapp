package com.infoq.myqapp.controller;

import com.infoq.myqapp.AuthenticationFilter;
import com.infoq.myqapp.domain.FeedEntry;
import com.infoq.myqapp.repository.FeedRepository;
import com.infoq.myqapp.service.MemberService;
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
    private MemberService memberService;

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
        Token requestToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_OAUTH_REQUEST_TOKEN, RequestAttributes.SCOPE_SESSION);
        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);
        LOG.info("Login attempt with request and access token : {} {}", requestToken, accessToken);

        boolean hasToAuthenticate = false;
        if (requestToken == null || accessToken == null) {
            hasToAuthenticate = true;
        } else {
            // check if the token is not revoked
            try {
                trelloService.getUserInfo(accessToken);
            } catch (HttpClientErrorException e) {
                hasToAuthenticate = true;
            }
        }

        if (hasToAuthenticate) {
            // generate new request token
            OAuthService service = trelloAuthenticationService.getService(httpServletRequest.getRequestURL()
                    .toString().replace("/api/trello/login", "/api/trello/callback"));

            requestToken = service.getRequestToken();
            request.setAttribute(AuthenticationFilter.ATTR_OAUTH_REQUEST_TOKEN, requestToken, RequestAttributes.SCOPE_SESSION);

            // redirect to trello auth page
            return "redirect:" + service.getAuthorizationUrl(requestToken);
        }
        return "redirect:/";
    }

    @RequestMapping(value = {"/callback"}, method = RequestMethod.GET)
    public String callback(@RequestParam(value = "oauth_verifier", required = false) String oauthVerifier, WebRequest request) {

        // getting request tocken
        OAuthService service = trelloAuthenticationService.getService();
        Token requestToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_OAUTH_REQUEST_TOKEN, RequestAttributes.SCOPE_SESSION);

        // getting access token
        Verifier verifier = new Verifier(oauthVerifier);
        Token accessToken = service.getAccessToken(requestToken, verifier);
        LOG.info("Access Granted to Trello with token {}", accessToken);

        // store access token as a session attribute
        request.setAttribute(AuthenticationFilter.ATTR_OAUTH_ACCESS_TOKEN, accessToken, RequestAttributes.SCOPE_SESSION);

        return "redirect:/";
    }

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public ResponseEntity getUserInfo(WebRequest request) {
        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);

        Member member = trelloService.getUserInfo(accessToken);
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @RequestMapping(value = "/member", method = RequestMethod.GET)
    public ResponseEntity getMembers(WebRequest request) {
        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);

        List<Member> members = memberService.getMembers(accessToken);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity handleUnauthorized(HttpClientErrorException e) {
        return new ResponseEntity(e.getStatusCode());
    }
}
