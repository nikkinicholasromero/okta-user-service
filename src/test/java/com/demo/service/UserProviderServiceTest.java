package com.demo.service;

import com.demo.exception.AuthenticationFailedException;
import com.demo.model.Profile;
import com.demo.model.User;
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
@RestClientTest(UserProviderService.class)
public class UserProviderServiceTest {
    @Autowired
    private UserProviderService target;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    private String userId;

    private String expectedUri;

    private User expected;

    @Before
    public void setup() {
        userId = "someUserId";

        expectedUri = "https://dev-462355.okta.com/api/v1/users/" + userId;

        Profile profile = new Profile();
        profile.setLogin("someUsername");
        profile.setFirstName("someFirstName");
        profile.setLastName("someLastName");

        expected = new User();
        expected.setProfile(profile);
    }

    @Test
    public void getUser_whenRequestIsSuccessful() throws JsonProcessingException {
        server.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "SSWS "))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(expected)));

        User actual = target.getUser(userId);

        assertThat(actual).isNotNull();
        assertThat(actual.getProfile()).isNotNull();
        assertThat(actual.getProfile().getLogin()).isEqualTo("someUsername");
        assertThat(actual.getProfile().getFirstName()).isEqualTo("someFirstName");
        assertThat(actual.getProfile().getLastName()).isEqualTo("someLastName");
    }

    @Test
    public void getUser_whenStatusIs4xx() {
        server.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "SSWS "))
                .andRespond(withUnauthorizedRequest());

        assertThrows(AuthenticationFailedException.class, () -> target.getUser(userId));
    }

    @Test
    public void getUser_whenStatusIs5xx() {
        server.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "SSWS "))
                .andRespond(withServerError());

        assertThrows(AuthenticationFailedException.class, () -> target.getUser(userId));
    }
}
