package com.infoq.myqapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.scribe.model.Token;
import org.springframework.data.annotation.Id;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

    @Id
    private String email;

    private String firstName;
    private String lastName;
    private List<String> authorities;

    @JsonIgnore
    private String tokenTrello;
    @JsonIgnore
    private String secretTrello;
    @JsonIgnore
    private String tokenGithub;
    @JsonIgnore
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
