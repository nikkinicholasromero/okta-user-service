package com.demo.service;

import com.demo.exception.AuthenticationFailedException;
import com.demo.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserProviderService {
    private final RestTemplate restTemplate;

    @Value("${okta.user.provider.host}")
    private String host;

    @Value("${okta.user.provider.endpoint}")
    private String endpoint;

    @Value("${okta.api.key}")
    private String apiKey;

    public UserProviderService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public User getUser(String userId) {
        try {
            return restTemplate.exchange(getUri(userId), HttpMethod.GET, getEntity(), User.class).getBody();
        } catch (Exception e) {
            throw new AuthenticationFailedException();
        }
    }

    private String getUri(String userId) {
        return String.format(host + endpoint, userId);
    }

    private HttpEntity<Void> getEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "SSWS " + apiKey);
        return new HttpEntity<>(headers);
    }
}
