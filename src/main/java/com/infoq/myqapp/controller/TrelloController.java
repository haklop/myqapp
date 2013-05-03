package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.FeedEntry;
import com.infoq.myqapp.domain.RequestResult;
import com.infoq.myqapp.service.TrelloAuthenticationService;
import com.infoq.myqapp.service.TrelloService;
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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/trello")
public class TrelloController {

    private static final Logger LOG = LoggerFactory.getLogger(TrelloController.class);

    private static final String ATTR_OAUTH_REQUEST_TOKEN = "oauthrequestoken";
    private static final String ATTR_OAUTH_ACCESS_TOKEN = "oauthaccesstoken";

    @Resource
    private TrelloAuthenticationService trelloAuthenticationService;

    @Resource
    private TrelloService trelloService;

    @RequestMapping(method = RequestMethod.POST, value = "/card")
    @ResponseBody
    public RequestResult addToTrello(@RequestBody FeedEntry feed, WebRequest request) throws Exception {
        LOG.info("Adding card to Trello {}", feed.getTitle());

        Token requestToken = (Token) request.getAttribute(ATTR_OAUTH_REQUEST_TOKEN, RequestAttributes.SCOPE_SESSION);
        Token accessToken = (Token) request.getAttribute(ATTR_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);

        if (requestToken == null || accessToken == null) {
            return new RequestResult("api/trello/login");
        }

        trelloService.addCardToTrello(feed, accessToken);

        return new RequestResult("");
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/lists")
    public RequestResult<List<TList>> getLists(WebRequest request) {
        Token requestToken = (Token) request.getAttribute(ATTR_OAUTH_REQUEST_TOKEN, RequestAttributes.SCOPE_SESSION);
        Token accessToken = (Token) request.getAttribute(ATTR_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);

        if (requestToken == null || accessToken == null) {
            return new RequestResult("api/trello/login");
        }
        return new RequestResult<>(trelloService.getLists(accessToken));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public String login(WebRequest request, HttpServletRequest httpServletRequest) throws Exception {
        Token requestToken = (Token) request.getAttribute(ATTR_OAUTH_REQUEST_TOKEN, RequestAttributes.SCOPE_SESSION);
        Token accessToken = (Token) request.getAttribute(ATTR_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);
        LOG.info("Login attempt with request and access token : {} {}", requestToken, accessToken);
        if (requestToken == null || accessToken == null) {
            // generate new request token
            trelloAuthenticationService.setRequestURL(httpServletRequest.getRequestURL().toString());
            OAuthService service = trelloAuthenticationService.getService();
            requestToken = service.getRequestToken();
            request.setAttribute(ATTR_OAUTH_REQUEST_TOKEN, requestToken, RequestAttributes.SCOPE_SESSION);

            // redirect to trello auth page
            return "redirect:" + service.getAuthorizationUrl(requestToken);
        }
        return "redirect:/";
    }

    @RequestMapping(value = {"/callback"}, method = RequestMethod.GET)
    public ModelAndView callback(@RequestParam(value = "oauth_verifier", required = false) String oauthVerifier, WebRequest request) {

        // getting request tocken
        OAuthService service = trelloAuthenticationService.getService();
        Token requestToken = (Token) request.getAttribute(ATTR_OAUTH_REQUEST_TOKEN, RequestAttributes.SCOPE_SESSION);

        // getting access token
        Verifier verifier = new Verifier(oauthVerifier);
        Token accessToken = service.getAccessToken(requestToken, verifier);
        LOG.info("Access Granted to Trello with token {}", accessToken);

        // store access token as a session attribute
        request.setAttribute(ATTR_OAUTH_ACCESS_TOKEN, accessToken, RequestAttributes.SCOPE_SESSION);

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public ResponseEntity getUserInfo(WebRequest request) {
        Token requestToken = (Token) request.getAttribute(ATTR_OAUTH_REQUEST_TOKEN, RequestAttributes.SCOPE_SESSION);
        Token accessToken = (Token) request.getAttribute(ATTR_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);

        if (requestToken == null || accessToken == null) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        Member member = trelloService.getMember("me", accessToken);
        return new ResponseEntity<>(member, HttpStatus.OK);
    }
}
