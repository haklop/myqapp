package com.infoq.myqapp.service;

import com.infoq.myqapp.service.scribe.TrelloApiProvider;
import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TrelloAuthenticationService {

    @Value("${trello.oauth.key}")
    private String trelloApplicationKey;

    @Value("${trello.oauth.secret}")
    private String trelloSecret;

    public OAuthService getService() {
        return getService("oob");
    }

    public OAuthService getService(String callbackUrl) {
        return new ServiceBuilder()
                .provider(TrelloApiProvider.class)
                .apiKey(trelloApplicationKey)
                .apiSecret(trelloSecret)
                .callback(callbackUrl)
                .build();
    }
}
