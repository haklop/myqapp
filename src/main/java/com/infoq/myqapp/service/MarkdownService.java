package com.infoq.myqapp.service;

import com.infoq.myqapp.domain.GitHubMarkdown;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MarkdownService {

    private RestTemplate restTemplate = new RestTemplate();

    public String generateHtml(GitHubMarkdown markdown) {
        String result =  restTemplate.postForObject("https://api.github.com/markdown", markdown, String.class);

        return processHtml(result);
    }

    private String processHtml(String html) {
        Document myAmazingContent = Jsoup.parse(html);

        for (Element octiconSpan : myAmazingContent.select("span.octicon")) {
            octiconSpan.remove();
        }
        return myAmazingContent.body().outerHtml().replace("<body>","").replace("</body>", "");
    }
}
