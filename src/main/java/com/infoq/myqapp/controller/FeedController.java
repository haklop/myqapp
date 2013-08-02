package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.FeedEntry;
import com.infoq.myqapp.repository.FeedRepository;
import com.infoq.myqapp.service.FeedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/feed")
@Secured("ROLE_EDITOR")
public class FeedController {

    @Resource
    private FeedService feedService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Page<FeedEntry> getAllFeeds() {
        return feedService.getPage(0);
    }

    @RequestMapping(value = "{page}", method = RequestMethod.GET)
    @ResponseBody
    public Page<FeedEntry> getPage(@PathVariable("page") int pageNumber) {
        return feedService.getPage(pageNumber);
    }

    @RequestMapping(value = "refresh", method = RequestMethod.GET)
    @ResponseBody
    public Page<FeedEntry> refreshAndGetNewFeed() {
        feedService.retrieveFeedTask();
        return getAllFeeds();
    }
}
