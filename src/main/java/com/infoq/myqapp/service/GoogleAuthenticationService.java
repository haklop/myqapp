package com.infoq.myqapp.service;

import com.infoq.myqapp.service.scribe.GoogleApiProvider;
import com.infoq.myqapp.service.scribe.TrelloApiProvider;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GoogleAuthenticationService {

    public static final String APPLICATION_KEY = "207009213428-etats02h37pkv8hhgav15i4k4rk2l744.apps.googleusercontent.com";
    public static final String OAUTH_SECRET = "-KNjbAL9sRf_8pkumUS-gPwJ";

    public OAuthService getService() {
        return new ServiceBuilder()
                .provider(GoogleApiProvider.class)
                .apiKey(APPLICATION_KEY)
                .apiSecret(OAUTH_SECRET)
                .scope("openid email")
                .callback("http://localhost:8080/api/google/callback")
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
