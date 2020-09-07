package com.demo.mock;

import com.demo.model.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class MockRestTemplate extends RestTemplate {
    @Override
    public <T> ResponseEntity<T> postForEntity(
            String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) {
        if ("https://dev-462355.okta.com/api/v1/authn".equals(url)) {
            UserCredential userCredential = (UserCredential) request;
            if ("someUsername".equals(userCredential.getUsername())) {
                OktaSession oktaSession = new OktaSession();
                oktaSession.setSessionToken("someSessionToken");
                return new ResponseEntity(oktaSession, HttpStatus.OK);
            } else {
                throw new RuntimeException();
            }
        }

        if ("https://dev-462355.okta.com/api/v1/sessions".equals(url)) {
            OktaSessionCookie oktaSessionCookie = new OktaSessionCookie();
            oktaSessionCookie.setId("someSessionCookieId");
            return new ResponseEntity(oktaSessionCookie, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @Override
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
                                          @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables)
            throws RestClientException {
        if ("https://dev-462355.okta.com/api/v1/sessions/someSessionCookie".equals(url)) {
            OktaSessionCookie oktaSessionCookie = new OktaSessionCookie();
            oktaSessionCookie.setId("someId");
            oktaSessionCookie.setStatus("someStatus");
            oktaSessionCookie.setUserId("userId");
            return new ResponseEntity(oktaSessionCookie, HttpStatus.OK);
        }

        if ("https://dev-462355.okta.com/api/v1/sessions/someInvalidSessionCookie".equals(url)) {
            throw new RuntimeException();
        }

        if ("https://dev-462355.okta.com/api/v1/users/userId".equals(url)) {
            Profile profile = new Profile();
            profile.setLogin("someUsername");
            profile.setFirstName("someFirstName");
            profile.setLastName("someLastName");

            User user = new User();
            user.setProfile(profile);

            return new ResponseEntity(user, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
