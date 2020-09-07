package com.demo.service;

import com.demo.exception.AuthenticationFailedException;
import com.demo.model.OktaSession;
import com.demo.model.UserCredential;
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
@RestClientTest(OktaAuthenticationService.class)
public class OktaAuthenticationServiceTest {
    @Autowired
    private OktaAuthenticationService target;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    private UserCredential userCredential;

    private String expectedUri;

    @Before
    public void setup() {
        userCredential = new UserCredential();
        userCredential.setUsername("someUsername");
        userCredential.setPassword("somePassword");

        expectedUri = "https://dev-462355.okta.com/api/v1/authn";
    }

    @Test
    public void authenticate_whenRequestIsSuccessful() throws JsonProcessingException {
        OktaSession expected = new OktaSession();
        expected.setSessionToken("someSessionToken");

        server.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(objectMapper.writeValueAsString(userCredential)))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(expected)));

        OktaSession actual = target.authenticate(userCredential);

        assertThat(actual).isNotNull();
        assertThat(actual.getSessionToken()).isEqualTo(expected.getSessionToken());
    }

    @Test
    public void authenticate_whenStatusIs4xx() throws JsonProcessingException {
        server.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(objectMapper.writeValueAsString(userCredential)))
                .andRespond(withUnauthorizedRequest());

        assertThrows(AuthenticationFailedException.class, () -> target.authenticate(userCredential));
    }

    @Test
    public void authenticate_whenStatusIs5xx() throws JsonProcessingException {
        server.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(objectMapper.writeValueAsString(userCredential)))
                .andRespond(withServerError());

        assertThrows(AuthenticationFailedException.class, () -> target.authenticate(userCredential));
    }
}
