package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.FeedEntry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/feed")
public class FeedController {

    @RequestMapping(method =  RequestMethod.GET)
    @ResponseBody
    public List<FeedEntry> getAllBoxes(){
        return new ArrayList<>();
    }
}
