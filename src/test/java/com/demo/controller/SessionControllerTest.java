package com.demo.controller;

import com.demo.model.SessionInformation;
import com.demo.orchestrator.SessionOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SessionControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private SessionController target;

    @Mock
    private SessionOrchestrator orchestrator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(target).build();

        SessionInformation expected = new SessionInformation();
        expected.setId("someId");
        expected.setStatus("someStatus");
        expected.setUsername("someUsername");
        expected.setFirstName("someFirstName");
        expected.setLastName("someLastName");

        when(orchestrator.orchestrate(anyString())).thenReturn(expected);
    }

    @Test
    public void getSessionInformation() throws Exception {
        mockMvc.perform(get("/sessions/someSessionCookie"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("someId"))
                .andExpect(jsonPath("$.status").value("someStatus"))
                .andExpect(jsonPath("$.username").value("someUsername"))
                .andExpect(jsonPath("$.firstName").value("someFirstName"))
                .andExpect(jsonPath("$.lastName").value("someLastName"));

        verify(orchestrator).orchestrate("someSessionCookie");
    }
}
