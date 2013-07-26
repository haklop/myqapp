package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.UserProfile;
import com.infoq.myqapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public ResponseEntity list() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public ResponseEntity create(@RequestBody @Valid UserProfile userProfile) {
        userService.create(userProfile);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{userId:.+}")
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public ResponseEntity update(@PathVariable String userId, @RequestBody @Valid UserProfile userProfile) {
        userService.update(userId, userProfile);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId:.+}")
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public ResponseEntity create(@PathVariable String userId) {
        userService.delete(userId);
        return new ResponseEntity(HttpStatus.OK);
    }

}
