package com.demo.service;

import com.demo.exception.AuthenticationFailedException;
import com.demo.model.OktaSessionCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SessionCookieValidationService {
    private final RestTemplate restTemplate;

    @Value("${okta.session.cookie.validation.host}")
    private String host;

    @Value("${okta.session.cookie.validation.endpoint}")
    private String endpoint;

    @Value("${okta.api.key}")
    private String apiKey;

    public SessionCookieValidationService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public OktaSessionCookie validateSessionCookie(String sessionCookie) {
        try {
            return restTemplate.exchange(getUri(sessionCookie), HttpMethod.GET, getEntity(), OktaSessionCookie.class).getBody();
        } catch (Exception e) {
            throw new AuthenticationFailedException();
        }
    }

    private String getUri(String sessionCookie) {
        return String.format(host + endpoint, sessionCookie);
    }

    private HttpEntity<Void> getEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "SSWS " + apiKey);
        return new HttpEntity<>(headers);
    }
}
