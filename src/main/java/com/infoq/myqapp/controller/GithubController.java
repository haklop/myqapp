package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.ErrorMessage;
import com.infoq.myqapp.domain.GitHubContent;
import com.infoq.myqapp.domain.UserProfile;
import com.infoq.myqapp.service.GithubAuthenticationService;
import com.infoq.myqapp.service.GithubService;
import com.infoq.myqapp.service.UserService;
import org.scribe.model.OAuthConstants;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
@RequestMapping("/github")
@Secured("ROLE_EDITOR")
public class GithubController {

    private static final Logger logger = LoggerFactory.getLogger(GithubController.class);

    @Resource
    private GithubAuthenticationService githubAuthenticationService;

    @Resource
    private GithubService githubService;

    @Resource
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public String login(WebRequest request) {
        String email = (String) request.getAttribute(GoogleController.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION);
        UserProfile userProfile = userService.get(email); // TODO: how to inject this value as a parameter of the method ?

        Token accessToken = userProfile.getTokenGithub();

        if (accessToken == null || accessToken.isEmpty()) {
            // generate new request token
            OAuthService service = githubAuthenticationService.getService();

            // redirect to trello auth page
            return "redirect:" + service.getAuthorizationUrl(OAuthConstants.EMPTY_TOKEN);
        }
        return "redirect:/";
    }

    @RequestMapping(value = {"/callback"}, method = RequestMethod.GET)
    public String callback(@RequestParam(value = "code", required = false) String oauthVerifier, WebRequest request) throws IOException {
        String email = (String) request.getAttribute(GoogleController.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION);

        UserProfile userProfile = userService.get(email);
        if (userProfile == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        OAuthService service = githubAuthenticationService.getService();

        Verifier verifier = new Verifier(oauthVerifier);
        Token accessToken = service.getAccessToken(OAuthConstants.EMPTY_TOKEN, verifier);
        logger.info("Access Granted to Github with token {}", accessToken);

        userProfile.setTokenGithub(accessToken);
        userService.save(userProfile);

        return "redirect:/";
    }

    @RequestMapping(value = {"/raw"}, method = RequestMethod.GET)
    public ResponseEntity getRaw(@RequestParam(value = "url", required = false) String url, WebRequest request) {
        String email = (String) request.getAttribute(GoogleController.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION);
        UserProfile userProfile = userService.get(email); // TODO: how to inject this value as a parameter of the method ?

        Token accessToken = userProfile.getTokenGithub();
        if (accessToken == null || accessToken.isEmpty()) {
            return new ResponseEntity<>(new ErrorMessage(HttpStatus.FORBIDDEN.value(), "githubToken", "GitHub token is missing"),
                    HttpStatus.FORBIDDEN);
        }

        GitHubContent raw;
        try {
            raw = githubService.getRaw(url, accessToken);
            // TODO catch error when using an expired token
            // TODO catch 403 rate limit exceeded
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode().value()) {
                case 401:
                    return new ResponseEntity<>(new ErrorMessage(HttpStatus.UNAUTHORIZED.value(), "githubToken", "You cannot access to the GitHub repository"),
                            HttpStatus.UNAUTHORIZED);
                default:
                    logger.warn("Unknown GitHub error", e);
                    return new ResponseEntity<>(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "githubToken", "You cannot access to the GitHub repository"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(raw, HttpStatus.OK);
    }
}
