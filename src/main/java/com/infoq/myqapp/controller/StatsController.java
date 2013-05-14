package com.infoq.myqapp.controller;

import com.infoq.myqapp.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/stats")
public class StatsController {

    @Resource
    private StatsService statsService;

    @RequestMapping(method = RequestMethod.GET, value = "/users")
    @ResponseBody
    public ResponseEntity getUsersStats() {
        statsService.getUsersStats();
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/lists")
    @ResponseBody
    public ResponseEntity getListsStats() {
        statsService.getListsStats();
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
