package com.demo.service;

import com.demo.exception.AuthenticationFailedException;
import com.demo.model.OktaSession;
import com.demo.model.UserCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OktaAuthenticationService {
    private final RestTemplate restTemplate;

    @Value("${okta.authentication.host}")
    private String host;

    @Value("${okta.authentication.endpoint}")
    private String endpoint;

    public OktaAuthenticationService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public OktaSession authenticate(UserCredential userCredential) {
        try {
            return restTemplate.postForEntity(host + endpoint, userCredential, OktaSession.class).getBody();
        } catch (Exception e) {
            throw new AuthenticationFailedException();
        }
    }
}
