package com.infoq.myqapp.service;

import org.pegdown.PegDownProcessor;
import org.springframework.stereotype.Service;

@Service
public class MarkdownService {

    private PegDownProcessor pegDownProcessor = new PegDownProcessor();

    public String generateHtml(String markdown){
        return pegDownProcessor.markdownToHtml(markdown);
    }
}
