package com.infoq.myqapp.service;

import com.infoq.myqapp.service.scribe.GithubApiProvider;
import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GithubAuthenticationService {

    @Value("${github.oauth.client.id}")
    private String clientId;

    @Value("${github.oauth.client.secret}")
    private String clientSecret;

    @Value("${github.oauth.callback}")
    private String callback;

    public OAuthService getService() {
        return new ServiceBuilder()
                .provider(GithubApiProvider.class)
                .apiKey(clientId)
                .apiSecret(clientSecret)
                .scope("repo")
                .callback(callback)
                .build();
    }
}
