package com.infoq.myqapp.service;

import com.infoq.myqapp.service.scribe.TrelloApiProvider;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TrelloApi;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TrelloAuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(TrelloAuthenticationService.class);

    public static final String APPLICATION_KEY = "642f4c01f3e706d24e66d89165b6b22f";
    public static final String OAUTH_SECRET = "d00aa977f055948e1a5f451a73e8bfeafc858f9973f26bb23fee0e61be737bcb";

    private String callBackURL;

    public void setRequestURL(String requestURL) {
        callBackURL = getCallbackURL(requestURL);
    }

    public OAuthService getService() {
        return new ServiceBuilder()
                .provider(TrelloApiProvider.class)
                .apiKey(APPLICATION_KEY)
                .apiSecret(OAUTH_SECRET)
                .callback(callBackURL)
                .build();
    }

    private String getCallbackURL(String requestURL) {
        return requestURL.replace("/api/trello/login", "/api/trello/callback");
    }
}
