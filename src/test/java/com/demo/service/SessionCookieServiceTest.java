package com.demo.service;

import com.demo.exception.AuthenticationFailedException;
import com.demo.model.OktaSessionClaim;
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
@RestClientTest(SessionCookieService.class)
public class SessionCookieServiceTest {
    @Autowired
    private SessionCookieService target;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    private OktaSessionClaim oktaSessionClaim;

    private String expectedUri;

    @Before
    public void setup() {
        oktaSessionClaim = new OktaSessionClaim("someSessionToken");

        expectedUri = "https://dev-462355.okta.com/api/v1/sessions";
    }

    @Test
    public void getSessionCookie_whenRequestIsSuccessful() throws JsonProcessingException {
        OktaSessionCookie expected = new OktaSessionCookie();
        expected.setId("someSessionCookie");

        server.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(objectMapper.writeValueAsString(oktaSessionClaim)))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(expected)));

        OktaSessionCookie actual = target.getSessionCookie(oktaSessionClaim);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(expected.getId());
    }

    @Test
    public void getSessionCookie_whenStatusIs4xx() throws JsonProcessingException {
        server.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(objectMapper.writeValueAsString(oktaSessionClaim)))
                .andRespond(withUnauthorizedRequest());

        assertThrows(AuthenticationFailedException.class, () -> target.getSessionCookie(oktaSessionClaim));
    }

    @Test
    public void getSessionCookie_whenStatusIs5xx() throws JsonProcessingException {
        server.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(objectMapper.writeValueAsString(oktaSessionClaim)))
                .andRespond(withServerError());

        assertThrows(AuthenticationFailedException.class, () -> target.getSessionCookie(oktaSessionClaim));
    }
}
