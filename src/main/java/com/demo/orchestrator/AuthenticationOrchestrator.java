package com.demo.orchestrator;

import com.demo.model.OktaSession;
import com.demo.model.OktaSessionClaim;
import com.demo.model.OktaSessionCookie;
import com.demo.model.UserCredential;
import com.demo.service.OktaAuthenticationService;
import com.demo.service.SessionCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationOrchestrator implements Orchestrator<UserCredential, OktaSessionCookie> {
    @Autowired
    private OktaAuthenticationService oktaAuthenticationService;

    @Autowired
    private SessionCookieService sessionCookieService;

    @Override
    public OktaSessionCookie orchestrate(UserCredential userCredential) {
        OktaSession oktaSession = oktaAuthenticationService.authenticate(userCredential);
        OktaSessionClaim oktaSessionClaim = new OktaSessionClaim(oktaSession.getSessionToken());
        return sessionCookieService.getSessionCookie(oktaSessionClaim);
    }
}
