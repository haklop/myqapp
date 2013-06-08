package com.infoq.myqapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoq.myqapp.AuthenticationFilter;
import com.infoq.myqapp.domain.UserProfile;
import com.infoq.myqapp.repository.UserProfileRepository;
import com.infoq.myqapp.service.GoogleAuthenticationService;
import com.infoq.myqapp.service.TrelloService;
import org.apache.commons.codec.binary.Base64;
import org.scribe.model.OAuthConstants;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/google")
public class GoogleController {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleController.class);

    private static final Set<String> ALLOWED_EMAIL = new HashSet<>();

    static {
        ALLOWED_EMAIL.add("eric.bellemon@gmail.com");
        ALLOWED_EMAIL.add("vey.julien@gmail.com");
        ALLOWED_EMAIL.add("simon.basle@zenika.com");
        ALLOWED_EMAIL.add("al-amine.ousman@zenika.com");
        ALLOWED_EMAIL.add("pierre@queinnec.org");
        ALLOWED_EMAIL.add("pierre.queinnec@gmail.com");
        ALLOWED_EMAIL.add("mathieu.pousse@zenika.com");
        ALLOWED_EMAIL.add("hadrien.pierart@zenika.com");
        ALLOWED_EMAIL.add("antoine.rouaze@gmail.com");
        ALLOWED_EMAIL.add("benoit.nouyrigat@zenika.com");
    }

    @Resource
    private GoogleAuthenticationService googleAuthenticationService;

    @Resource
    private TrelloService trelloService;

    @Resource
    private UserProfileRepository userProfileRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public String login(WebRequest request) {
        Token accessToken = (Token) request.getAttribute(AuthenticationFilter.ATTR_GOOGLE_OAUTH_ACCESS_TOKEN, RequestAttributes.SCOPE_SESSION);
        LOG.info("Login attempt with access token : {} ", accessToken);

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
        LOG.info("Access Granted to Google with token {}", accessToken);

        String idToken = googleAuthenticationService.getIdToken(accessToken.getRawResponse());
        String userIdToken = idToken.split("\\.")[1];

        ObjectMapper mapper = new ObjectMapper();
        byte[] decodedId = Base64.decodeBase64(userIdToken);
        String decodedIdString = new String(decodedId);
        UserProfile profileFromGoogle = mapper.readValue(decodedIdString, UserProfile.class);

        UserProfile profileFromMongo = userProfileRepository.findOne(profileFromGoogle.getEmail());

        if (profileFromMongo == null) {
            // first connection
            if (!ALLOWED_EMAIL.contains(profileFromGoogle.getEmail())) {
                return "redirect:/error-403.html";
            } else {
                request.setAttribute(AuthenticationFilter.ATTR_GOOGLE_OAUTH_ACCESS_TOKEN, accessToken, RequestAttributes.SCOPE_SESSION);
                request.setAttribute(AuthenticationFilter.ATTR_GOOGLE_EMAIL, profileFromGoogle.getEmail(), RequestAttributes.SCOPE_SESSION);
                userProfileRepository.save(profileFromGoogle);

                return "redirect:/trello-token.html";
            }
        } else {
            Token tokenTrello = profileFromMongo.getTokenTrello();
            Token tokenGithub = profileFromMongo.getTokenGithub();

            request.setAttribute(AuthenticationFilter.ATTR_GOOGLE_OAUTH_ACCESS_TOKEN, accessToken, RequestAttributes.SCOPE_SESSION);
            request.setAttribute(AuthenticationFilter.ATTR_GOOGLE_EMAIL, profileFromGoogle.getEmail(), RequestAttributes.SCOPE_SESSION);

            if (tokenTrello != null) {
                request.setAttribute(AuthenticationFilter.ATTR_TRELLO_OAUTH_ACCESS_TOKEN, tokenTrello, RequestAttributes.SCOPE_SESSION);
            }
            if (tokenGithub != null) {
                request.setAttribute(AuthenticationFilter.ATTR_GITHUB_OAUTH_ACCESS_TOKEN, tokenGithub, RequestAttributes.SCOPE_SESSION);
            }

            if (tokenTrello == null) {
                return "redirect:/trello-token.html";
            } else if (tokenGithub == null) {
                return "redirect:/github-token.html";
            } else {
                // check if the token is not revoked
                try {
                    trelloService.getUserInfo(profileFromMongo.getTokenTrello());
                } catch (HttpClientErrorException e) {
                    return "redirect:/trello-token.html";
                }
            }

        }

        return "redirect:/";
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity handleClientErrorException(HttpClientErrorException e) {
        return new ResponseEntity(e.getStatusCode());
    }
}
