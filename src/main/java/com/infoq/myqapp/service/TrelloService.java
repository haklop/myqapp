package com.infoq.myqapp.service;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TrelloApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import org.springframework.stereotype.Service;

@Service
public class TrelloService {

    public static final String APPLICATION_KEY = "642f4c01f3e706d24e66d89165b6b22f";

    public static final String OAUTH_SECRET = "d00aa977f055948e1a5f451a73e8bfeafc858f9973f26bb23fee0e61be737bcb";

    public String getToken() {
        OAuthService service = new ServiceBuilder()
                .provider(TrelloApi.class)
                .apiKey(APPLICATION_KEY)
                .apiSecret(OAUTH_SECRET)
                .build();

        Token requestToken = service.getRequestToken();

        System.out.println(service.getAuthorizationUrl(requestToken));

        return "";
    }
}
