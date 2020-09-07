package com.demo.orchestrator;

import com.demo.model.OktaSessionCookie;
import com.demo.model.Profile;
import com.demo.model.SessionInformation;
import com.demo.model.User;
import com.demo.service.SessionCookieValidationService;
import com.demo.service.UserProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SessionOrchestratorTest {
    @InjectMocks
    private SessionOrchestrator target;

    @Mock
    private SessionCookieValidationService sessionCookieValidationService;

    @Mock
    private UserProviderService userProviderService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        OktaSessionCookie oktaSessionCookie = new OktaSessionCookie();
        oktaSessionCookie.setId("someSessionCookieId");
        oktaSessionCookie.setStatus("someStatus");
        oktaSessionCookie.setUserId("someUserId");

        Profile profile = new Profile();
        profile.setLogin("someUsername");
        profile.setFirstName("someFirstName");
        profile.setLastName("someLastName");

        User user = new User();
        user.setProfile(profile);

        when(sessionCookieValidationService.validateSessionCookie(anyString())).thenReturn(oktaSessionCookie);
        when(userProviderService.getUser(anyString())).thenReturn(user);
    }

    @Test
    public void orchestrate() {
        SessionInformation actual = target.orchestrate("someSessionCookie");

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo("someSessionCookieId");
        assertThat(actual.getStatus()).isEqualTo("someStatus");
        assertThat(actual.getUsername()).isEqualTo("someUsername");
        assertThat(actual.getFirstName()).isEqualTo("someFirstName");
        assertThat(actual.getLastName()).isEqualTo("someLastName");

        verify(sessionCookieValidationService).validateSessionCookie("someSessionCookie");
        verify(userProviderService).getUser("someUserId");
    }
}
