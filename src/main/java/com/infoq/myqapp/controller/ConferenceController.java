package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.Conference;
import com.infoq.myqapp.service.ConferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/conf")
public class ConferenceController {

    @Resource
    private ConferenceService conferenceService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity createConf(@RequestBody Conference conference) {
        conferenceService.createConf(conference);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Conference> getAllConfs() {
        return conferenceService.getAllFutureConfs();
    }

    @RequestMapping(method = RequestMethod.GET, value = "{year:[0-9]{4}}/{month:[0-9]{2}}")
    @ResponseBody
    public List<Conference> getConfsByMonth(@PathVariable("year") int year, @PathVariable("month") int month) {
        return conferenceService.getConfsByMonth(year, month);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/period")
    @ResponseBody
    public List<Conference> getConfsByTimestamp(@RequestParam("start") long start, @RequestParam("end") long end) {
        return conferenceService.getConfsByTimestamp(start, end);
    }
}
