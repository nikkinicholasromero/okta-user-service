package com.demo.mock;

import com.demo.model.OktaSession;
import com.demo.model.OktaSessionCookie;
import com.demo.model.UserCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
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
}
