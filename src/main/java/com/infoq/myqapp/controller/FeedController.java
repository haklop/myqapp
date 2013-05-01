package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.FeedEntry;
import com.infoq.myqapp.service.FeedService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/feed")
public class FeedController {

    @Resource
    private FeedService feedService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<FeedEntry> getAllBoxes() throws Exception {
        return feedService.readFeed();
    }
}
