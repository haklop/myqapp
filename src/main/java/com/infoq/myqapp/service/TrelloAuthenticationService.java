package com.infoq.myqapp.service;

import com.infoq.myqapp.service.scribe.TrelloApiProvider;
import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;
import org.springframework.stereotype.Service;

@Service
public class TrelloAuthenticationService {

    public static final String APPLICATION_KEY = "642f4c01f3e706d24e66d89165b6b22f";
    public static final String OAUTH_SECRET = "d00aa977f055948e1a5f451a73e8bfeafc858f9973f26bb23fee0e61be737bcb";

    public OAuthService getService() {
        return getService("oob");
    }

    public OAuthService getService(String callbackUrl) {
        return new ServiceBuilder()
                .provider(TrelloApiProvider.class)
                .apiKey(APPLICATION_KEY)
                .apiSecret(OAUTH_SECRET)
                .callback(callbackUrl)
                .build();
    }
}
