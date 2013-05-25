package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.GeneratedHtml;
import com.infoq.myqapp.domain.MyQAppMarkdown;
import com.infoq.myqapp.service.MarkdownService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/markdown")
public class MarkdownController {

    @Resource
    private MarkdownService markdownService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity generateHtml(@RequestBody MyQAppMarkdown markdown) {
        String html = markdownService.generateHtml(markdown);
        return new ResponseEntity<>(new GeneratedHtml(html), HttpStatus.OK);
    }

}
