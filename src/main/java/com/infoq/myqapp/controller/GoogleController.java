package com.infoq.myqapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoq.myqapp.domain.UserProfile;
import com.infoq.myqapp.service.GoogleAuthenticationService;
import com.infoq.myqapp.service.TrelloService;
import com.infoq.myqapp.service.UserService;
import org.apache.commons.codec.binary.Base64;
import org.scribe.model.OAuthConstants;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
@RequestMapping("/google")
@Secured("ROLE_ANONYMOUS")
public class GoogleController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleController.class);
    public static final String ATTR_GOOGLE_OAUTH_ACCESS_TOKEN = "googleoauthaccesstoken";
    public static final String ATTR_GOOGLE_EMAIL = "googleemail";

    @Resource
    private GoogleAuthenticationService googleAuthenticationService;

    @Resource
    private AuthenticationManager authenticationManager;

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public String login(WebRequest request) {
        Token accessToken = (Token) request.getAttribute(ATTR_GOOGLE_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);
        logger.info("Login attempt with access token : {} ", accessToken);

        if (accessToken == null) {
            // generate new request token
            OAuthService service = googleAuthenticationService.getService();

            // redirect to trello auth page
            return "redirect:" + service.getAuthorizationUrl(OAuthConstants.EMPTY_TOKEN);
        }
        return "redirect:/";
    }

    @RequestMapping(value = {"/callback"}, method = RequestMethod.GET)
    public String callback(@RequestParam(value = "code", required = false) String oauthVerifier, WebRequest request) throws IOException {

        OAuthService service = googleAuthenticationService.getService();

        Verifier verifier = new Verifier(oauthVerifier);
        Token accessToken = service.getAccessToken(OAuthConstants.EMPTY_TOKEN, verifier);
        logger.info("Access Granted to Google with token {}", accessToken);

        String idToken = googleAuthenticationService.getIdToken(accessToken.getRawResponse());
        String userIdToken = idToken.split("\\.")[1];

        ObjectMapper mapper = new ObjectMapper();
        byte[] decodedId = Base64.decodeBase64(userIdToken);
        String decodedIdString = new String(decodedId);
        UserProfile profileFromGoogle = mapper.readValue(decodedIdString, UserProfile.class);

        try {
            Authentication authenticationRequest = new PreAuthenticatedAuthenticationToken(profileFromGoogle.getEmail(), null);
            Authentication result = authenticationManager.authenticate(authenticationRequest);
            SecurityContextHolder.getContext().setAuthentication(result);

            request.setAttribute(ATTR_GOOGLE_OAUTH_ACCESS_TOKEN, accessToken, RequestAttributes.SCOPE_SESSION);
            request.setAttribute(ATTR_GOOGLE_EMAIL, profileFromGoogle.getEmail(), RequestAttributes.SCOPE_SESSION);

        } catch (AuthenticationException e) {
            return "redirect:/error-403.html";
        }

        return "redirect:/";
    }
}
