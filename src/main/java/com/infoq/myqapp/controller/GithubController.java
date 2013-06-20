package com.infoq.myqapp.controller;

import com.infoq.myqapp.AuthenticationFilter;
import com.infoq.myqapp.domain.GitHubContent;
import com.infoq.myqapp.domain.UserProfile;
import com.infoq.myqapp.domain.ValueObject;
import com.infoq.myqapp.repository.UserProfileRepository;
import com.infoq.myqapp.service.GithubAuthenticationService;
import com.infoq.myqapp.service.GithubService;
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
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping("/github")
public class GithubController {

    private static final Logger LOG = LoggerFactory.getLogger(GithubController.class);

    @Resource
    private GithubAuthenticationService githubAuthenticationService;

    @Resource
    private GithubService githubService;

    @Resource
    private UserProfileRepository userProfileRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public String login(WebRequest request, HttpServletRequest httpServletRequest) {
        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_GITHUB_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);
        LOG.info("Github Login attempt with access token : {} ", accessToken);

        if (accessToken == null) {
            // generate new request token
            OAuthService service = githubAuthenticationService.getService();

            // redirect to trello auth page
            return "redirect:" + service.getAuthorizationUrl(OAuthConstants.EMPTY_TOKEN);
        }
        return "redirect:/";
    }

    @RequestMapping(value = {"/callback"}, method = RequestMethod.GET)
    public String callback(@RequestParam(value = "code", required = false) String oauthVerifier, WebRequest request) throws IOException {
        String email = (String) request.getAttribute(AuthenticationFilter.ATTR_GOOGLE_EMAIL, RequestAttributes.SCOPE_SESSION);

        UserProfile userProfile = userProfileRepository.findOne(email);
        if (userProfile == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        OAuthService service = githubAuthenticationService.getService();

        Verifier verifier = new Verifier(oauthVerifier);
        Token accessToken = service.getAccessToken(OAuthConstants.EMPTY_TOKEN, verifier);
        LOG.info("Access Granted to Github with token {}", accessToken);

        request.setAttribute(AuthenticationFilter.ATTR_GITHUB_OAUTH_ACCESS_TOKEN, accessToken, RequestAttributes.SCOPE_SESSION);

        userProfile.setTokenGithub(accessToken);
        userProfileRepository.save(userProfile);

        return "redirect:/";
    }

    @RequestMapping(value = {"/raw"}, method = RequestMethod.GET)
    @Secured("ROLE_EDITOR")
    public ResponseEntity<GitHubContent> getRaw(@RequestParam(value = "url", required = false) String url, WebRequest request){

        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_GITHUB_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);

        return new ResponseEntity<>(githubService.getRaw(url, accessToken), HttpStatus.OK);
    }
}
