package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.Conference;
import com.infoq.myqapp.service.ConferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/conf")
@Secured("ROLE_EDITOR")
public class ConferenceController {

    @Resource
    private ConferenceService conferenceService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity createConf(@RequestBody @Valid Conference conference) {
        conferenceService.createOrUpdate(conference);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{confId:.+}")
    @ResponseBody
    public ResponseEntity updateConf(@RequestBody @Valid Conference conference, @PathVariable String confId) {
        if (!conference.getId().equals(confId)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        conferenceService.createOrUpdate(conference);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{confId:.+}")
    @ResponseBody
    public ResponseEntity deleteConf(@PathVariable String confId) {
        conferenceService.delete(confId);
        return new ResponseEntity(HttpStatus.OK);
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
