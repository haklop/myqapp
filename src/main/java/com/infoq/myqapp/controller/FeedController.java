package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.FeedEntry;
import com.infoq.myqapp.repository.FeedRepository;
import com.infoq.myqapp.service.FeedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/feed")
public class FeedController {

    @Resource
    private FeedRepository feedRepository;

    @Resource
    private FeedService feedService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<FeedEntry> getAllFeeds() {
        Page<FeedEntry> page = feedRepository.findAll(new PageRequest(0, 20, Sort.Direction.DESC, "publishedDate"));
        return page.getContent();
    }

    @RequestMapping(value = "refresh", method = RequestMethod.GET)
    @ResponseBody
    public List<FeedEntry> refreshAndGetNewFeed() {
        return feedService.retrieveFeedTask();
    }
}
