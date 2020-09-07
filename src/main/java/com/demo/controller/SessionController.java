package com.demo.controller;

import com.demo.model.SessionInformation;
import com.demo.orchestrator.SessionOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    @Autowired
    private SessionOrchestrator orchestrator;

    @GetMapping("/{sessionCookie}")
    public SessionInformation getSessionInformation(@PathVariable("sessionCookie") String sessionCookie) {
        return orchestrator.orchestrate(sessionCookie);
    }
}
