package com.demo.mock;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

public class MockRestTemplateBuilder extends RestTemplateBuilder {
    @Override
    public RestTemplate build() {
        return new MockRestTemplate();
    }
}
