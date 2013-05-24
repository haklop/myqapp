package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.GitHubMarkdown;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MarkdownService {

    private RestTemplate restTemplate = new RestTemplate();

    public String generateHtml(GitHubMarkdown markdown) {
        return restTemplate.postForObject("https://api.github.com/markdown", markdown, String.class);
    }
}
