package com.demo.orchestrator;

import com.demo.model.OktaSessionCookie;
import com.demo.model.SessionInformation;
import com.demo.model.User;
import com.demo.service.SessionCookieValidationService;
import com.demo.service.UserProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionOrchestrator implements Orchestrator<String, SessionInformation> {
    @Autowired
    private SessionCookieValidationService sessionCookieValidationService;

    @Autowired
    private UserProviderService userProviderService;

    @Override
    public SessionInformation orchestrate(String sessionCookie) {
        OktaSessionCookie oktaSessionCookie = sessionCookieValidationService.validateSessionCookie(sessionCookie);
        SessionInformation sessionInformation = new SessionInformation();
        sessionInformation.setId(oktaSessionCookie.getId());
        sessionInformation.setStatus(oktaSessionCookie.getStatus());

        User user = userProviderService.getUser(oktaSessionCookie.getUserId());
        sessionInformation.setUsername(user.getProfile().getLogin());
        sessionInformation.setFirstName(user.getProfile().getFirstName());
        sessionInformation.setLastName(user.getProfile().getLastName());
        return sessionInformation;
    }
}
