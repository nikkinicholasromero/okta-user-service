package com.demo.service;

import com.demo.exception.AuthenticationFailedException;
import com.demo.model.OktaSessionClaim;
import com.demo.model.OktaSessionCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SessionCookieService {
    private final RestTemplate restTemplate;

    @Value("${okta.session.cookie.host}")
    private String host;

    @Value("${okta.session.cookie.endpoint}")
    private String endpoint;

    public SessionCookieService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public OktaSessionCookie getSessionCookie(OktaSessionClaim oktaSessionClaim) {
        try {
            return restTemplate.postForEntity(host + endpoint, oktaSessionClaim, OktaSessionCookie.class).getBody();
        } catch (Exception e) {
            throw new AuthenticationFailedException();
        }
    }
}
