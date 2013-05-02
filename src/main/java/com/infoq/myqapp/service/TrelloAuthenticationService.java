package com.infoq.myqapp.service;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TrelloApi;
import org.scribe.oauth.OAuthService;
import org.springframework.stereotype.Service;

@Service
public class TrelloAuthenticationService {

    public static final String APPLICATION_KEY = "642f4c01f3e706d24e66d89165b6b22f";

    public static final String OAUTH_SECRET = "d00aa977f055948e1a5f451a73e8bfeafc858f9973f26bb23fee0e61be737bcb";

    public OAuthService getService() {
        return new ServiceBuilder()
                .provider(TrelloApi.class)
                .apiKey(APPLICATION_KEY)
                .apiSecret(OAUTH_SECRET)
                .callback("http://localhost:8080/myqapp/api/trello/callback/")
                .build();
    }
}
