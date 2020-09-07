package com.demo.controller;

import com.demo.model.OktaSessionCookie;
import com.demo.model.UserCredential;
import com.demo.orchestrator.AuthenticationOrchestrator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private AuthenticationController target;

    @Mock
    private AuthenticationOrchestrator orchestrator;

    @Captor
    private ArgumentCaptor<UserCredential> userCredentialArgumentCaptor;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(target).build();

        objectMapper = new ObjectMapper();

        OktaSessionCookie oktaSessionCookie = new OktaSessionCookie();
        oktaSessionCookie.setId("someSessionCookieId");
        when(orchestrator.orchestrate(any(UserCredential.class))).thenReturn(oktaSessionCookie);
    }

    @Test
    public void authenticate() throws Exception {
        UserCredential userCredential = new UserCredential();
        userCredential.setUsername("someUsername");
        userCredential.setPassword("somePassword");

        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCredential)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", "sessionCookie=someSessionCookieId; Secure; HttpOnly"));

        verify(orchestrator).orchestrate(userCredentialArgumentCaptor.capture());

        UserCredential actual = userCredentialArgumentCaptor.getValue();
        assertThat(actual).isEqualTo(userCredential);
    }
}
