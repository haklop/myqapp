package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.Conference;
import com.infoq.myqapp.repository.ConferenceRepository;
import com.infoq.myqapp.service.ConferenceService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
        return conferenceService.getAllConfs();
    }
}
