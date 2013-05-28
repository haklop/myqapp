package com.infoq.myqapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.scribe.model.Token;
import org.springframework.data.annotation.Id;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

    @Id
    private String email;
    private String tokenTrello;
    private String secretTrello;
    private String tokenGithub;
    private String secretGithub;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Token getTokenTrello() {
        if (tokenTrello == null || secretTrello == null) {
            return null;
        }
        return new Token(tokenTrello, secretTrello);
    }

    public void setTokenTrello(Token tokenTrello) {
        this.tokenTrello = tokenTrello.getToken();
        this.secretTrello = tokenTrello.getSecret();
    }

    public Token getTokenGithub() {
        if (tokenGithub == null || secretGithub == null) {
            return null;
        }
        return new Token(tokenGithub, secretGithub);
    }

    public void setTokenGithub(Token tokenGithub) {
        this.tokenGithub = tokenGithub.getToken();
        this.secretGithub = tokenGithub.getSecret();
    }
}
