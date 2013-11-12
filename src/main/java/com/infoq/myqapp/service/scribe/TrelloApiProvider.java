package com.infoq.myqapp.service.scribe;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class TrelloApiProvider extends DefaultApi10a {
    private static final String AUTHORIZE_URL = "https://trello.com/1/OAuthAuthorizeToken?scope=read,write&expiration=never&oauth_token=%s&name=MyQApp";

    @Override
    public String getAccessTokenEndpoint() {
        return "https://trello.com/1/OAuthGetAccessToken";
    }

    @Override
    public String getRequestTokenEndpoint() {
        return "https://trello.com/1/OAuthGetRequestToken";
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return String.format(AUTHORIZE_URL, requestToken.getToken());
    }

}

