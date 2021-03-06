package com.infoq.myqapp.controller;

import com.infoq.myqapp.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/stats")
@Secured("ROLE_EDITOR")
public class StatsController {

    @Resource
    private StatsService statsService;

    @RequestMapping(method = RequestMethod.GET, value = "/users")
    @ResponseBody
    public ResponseEntity getUsersStats(HttpServletRequest request) {
        boolean canRetrieveActivity = request.isUserInRole("ROLE_ADMIN");
        return new ResponseEntity<>(statsService.getUsersStats(canRetrieveActivity), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/refresh")
    @ResponseBody
    public ResponseEntity refreshStats() {
        statsService.calculateStats();
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/lists")
    @ResponseBody
    public ResponseEntity getListsStats() {
        return new ResponseEntity<>(statsService.getListsStats(), HttpStatus.OK);
    }
}
