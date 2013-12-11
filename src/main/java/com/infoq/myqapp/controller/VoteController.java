package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.Vote;
import com.infoq.myqapp.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Controller
@RequestMapping("/vote")
public class VoteController {

    @Resource
    private VoteService voteService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Secured("ROLE_EDITOR")
    public ResponseEntity list() {
        return new ResponseEntity<>(voteService.getAllVotes(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public ResponseEntity create(@RequestBody @Valid Vote vote) {
        voteService.create(vote);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST,  value = "/{userId:.+}/close")
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public ResponseEntity create(@PathVariable String userId) {
        voteService.closeVote(userId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{userId:.+}/for")
    @ResponseBody
    @Secured("ROLE_EDITOR")
    public ResponseEntity voteFor(@PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        voteService.addVoterFor(userId, (String) authentication.getPrincipal());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{userId:.+}/against")
    @ResponseBody
    @Secured("ROLE_EDITOR")
    public ResponseEntity voteAgainst(@PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        voteService.addVoterAgainst(userId, (String) authentication.getPrincipal());
        return new ResponseEntity(HttpStatus.OK);
    }





}
