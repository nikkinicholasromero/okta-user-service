package com.demo;

import com.demo.mock.MockRestTemplateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {
    @Autowired
    private Environment environment;

    @Bean
    @Profile("mock")
    public RestTemplateBuilder restTemplateBuilder() {
        return new MockRestTemplateBuilder();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String origins = environment.getProperty("cors.allowed-origins");
        registry.addMapping("/**")
                .allowedOrigins(origins.split(","));
    }
}
