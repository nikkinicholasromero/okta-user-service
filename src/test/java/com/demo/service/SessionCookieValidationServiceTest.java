package com.demo.service;

import com.demo.exception.AuthenticationFailedException;
import com.demo.model.OktaSessionCookie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RunWith(SpringRunner.class)
@RestClientTest(SessionCookieValidationService.class)
public class SessionCookieValidationServiceTest {
    @Autowired
    private SessionCookieValidationService target;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    private String sessionCookie;

    private String expectedUri;

    private OktaSessionCookie expected;

    @Before
    public void setup() {
        sessionCookie = "someSessionCookie";

        expectedUri = "https://dev-462355.okta.com/api/v1/sessions/" + sessionCookie;

        expected = new OktaSessionCookie();
        expected.setId("someId");
        expected.setStatus("someStatus");
        expected.setUserId("someUserId");
    }

    @Test
    public void validateSessionCookie_whenRequestIsSuccessful() throws JsonProcessingException {
        server.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "SSWS "))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(expected)));

        OktaSessionCookie actual = target.validateSessionCookie(sessionCookie);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
        assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
    }

    @Test
    public void validateSessionCookie_whenStatusIs4xx() {
        server.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "SSWS "))
                .andRespond(withUnauthorizedRequest());

        assertThrows(AuthenticationFailedException.class, () -> target.validateSessionCookie(sessionCookie));
    }

    @Test
    public void validateSessionCookie_whenStatusIs5xx() {
        server.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "SSWS "))
                .andRespond(withServerError());

        assertThrows(AuthenticationFailedException.class, () -> target.validateSessionCookie(sessionCookie));
    }
}
