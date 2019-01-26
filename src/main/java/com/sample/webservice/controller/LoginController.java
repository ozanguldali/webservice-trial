package com.sample.webservice.controller;

import com.sample.webservice.model.LoginRequest;
import com.sample.webservice.model.LoginResponse;
import com.sample.webservice.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    LoginService loginService;

    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public LoginResponse result(@RequestBody LoginRequest loginRequest) {

        return loginService.getResult( loginRequest );

    }

}