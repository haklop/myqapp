package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.GitHubUser;
import com.infoq.myqapp.domain.ValueObject;
import org.scribe.model.Token;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GithubService {

    public ValueObject getRaw(String url, Token accessToken) {
        String rawUrl = getRawUrl(url).concat("?login={login}&token={token}");

        GitHubUser user = getUser(accessToken);

        RestTemplate restTemplate = new RestTemplate();
        String raw = restTemplate.getForObject(rawUrl, String.class, user.getLogin(), accessToken.getToken());

        return new ValueObject(raw);
    }

    public GitHubUser getUser(Token accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("https://api.github.com/user?access_token={token}", GitHubUser.class, accessToken.getToken());
    }

    private String getRawUrl(String url) {
        return url.replace("github.com/Zenika/TheQ/blob/master/", "raw.github.com/Zenika/TheQ/master/");
    }
}
