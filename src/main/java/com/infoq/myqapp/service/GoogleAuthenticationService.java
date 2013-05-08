package com.infoq.myqapp.service;

import com.infoq.myqapp.service.scribe.GoogleApiProvider;
import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GoogleAuthenticationService {

    @Value("${google.oauth.key}")
    private String applicationKey;

    @Value("${google.oauth.secret}")
    private String oauthSecret;

    @Value("${google.oauth.callback}")
    private String callback;

    public OAuthService getService() {
        return new ServiceBuilder()
                .provider(GoogleApiProvider.class)
                .apiKey(applicationKey)
                .apiSecret(oauthSecret)
                .scope("openid email")
                .callback(callback)
                .build();
    }

    public String getIdToken(String rawResponse) {
        Matcher matcher = Pattern.compile("\"id_token\" : \"([^&\"]+)\"").matcher(rawResponse);
        if (matcher.find()) {
            return OAuthEncoder.decode(matcher.group(1));
        } else {
            throw new RuntimeException("Cannot extract id_token");
        }
    }
}
