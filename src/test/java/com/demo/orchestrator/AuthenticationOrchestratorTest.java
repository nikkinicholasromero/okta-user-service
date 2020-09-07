package com.demo.orchestrator;

import com.demo.model.OktaSession;
import com.demo.model.OktaSessionClaim;
import com.demo.model.OktaSessionCookie;
import com.demo.model.UserCredential;
import com.demo.service.OktaAuthenticationService;
import com.demo.service.SessionCookieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthenticationOrchestratorTest {
    @InjectMocks
    private AuthenticationOrchestrator target;

    @Mock
    private OktaAuthenticationService oktaAuthenticationService;

    @Mock
    private SessionCookieService sessionCookieService;

    @Captor
    private ArgumentCaptor<OktaSessionClaim> oktaSessionClaimArgumentCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        OktaSession oktaSession = new OktaSession();
        oktaSession.setSessionToken("someSessionToken");

        OktaSessionCookie oktaSessionCookie = new OktaSessionCookie();
        oktaSessionCookie.setId("someSessionCookieId");

        when(oktaAuthenticationService.authenticate(any(UserCredential.class))).thenReturn(oktaSession);
        when(sessionCookieService.getSessionCookie(any(OktaSessionClaim.class))).thenReturn(oktaSessionCookie);
    }

    @Test
    public void orchestrate() {
        UserCredential userCredential = new UserCredential();
        userCredential.setUsername("someUsername");
        userCredential.setPassword("somePassword");

        OktaSessionCookie actual = target.orchestrate(userCredential);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo("someSessionCookieId");

        verify(oktaAuthenticationService).authenticate(userCredential);
        verify(sessionCookieService).getSessionCookie(oktaSessionClaimArgumentCaptor.capture());

        OktaSessionClaim oktaSessionClaim = oktaSessionClaimArgumentCaptor.getValue();
        assertThat(oktaSessionClaim).isNotNull();
        assertThat(oktaSessionClaim.getSessionToken()).isEqualTo("someSessionToken");
    }
}
