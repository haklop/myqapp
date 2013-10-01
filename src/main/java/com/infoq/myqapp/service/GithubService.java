package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.GitHubContent;
import com.infoq.myqapp.domain.GitHubUser;
import org.apache.commons.codec.binary.Base64;
import org.scribe.model.Token;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GithubService {

    private static final String GITHUB_API_REPO_URL = "https://api.github.com/repos/pmq/TheQ/contents/";
    private static final String GITHUB_REPO_MASTER_URL = "https://github.com/pmq/TheQ/blob/master/";
    private static final String ACCESS_TOKEN_QUERY_STRING = "?access_token={accessToken}";

    public GitHubContent getRaw(String url, Token accessToken) {
        if (!url.startsWith(GITHUB_REPO_MASTER_URL)) {
            throw new IllegalStateException("Fetched content should be taken from branch master");
        }
        String rawUrl = getRawUrl(url);

        RestTemplate restTemplate = new RestTemplate();
        GitHubContent content = restTemplate.getForObject(GITHUB_API_REPO_URL + rawUrl + ACCESS_TOKEN_QUERY_STRING, GitHubContent.class, accessToken.getToken());

        content.setContent(new String(Base64.decodeBase64(content.getContent())));

        return content;
    }

    public GitHubUser getUser(Token accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("https://api.github.com/user?access_token={token}", GitHubUser.class, accessToken.getToken());
    }

    private String getRawUrl(String url) {
        return url.replace(GITHUB_REPO_MASTER_URL, "");
    }
}
