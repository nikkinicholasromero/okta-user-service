package com.demo.controller;

import com.demo.model.OktaSessionCookie;
import com.demo.model.UserCredential;
import com.demo.orchestrator.AuthenticationOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationOrchestrator orchestrator;

    @PostMapping("")
    public String authenticate(@RequestBody UserCredential userCredential) {
        OktaSessionCookie oktaSessionCookie = orchestrator.orchestrate(userCredential);
        return oktaSessionCookie.getId();
    }
}
