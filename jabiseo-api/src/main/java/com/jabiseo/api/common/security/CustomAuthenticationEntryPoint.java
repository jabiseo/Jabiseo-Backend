package com.jabiseo.api.common.security;

import com.jabiseo.api.common.ResponseFactory;
import com.jabiseo.domain.auth.exception.AuthenticationErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ResponseFactory.fail(response, AuthenticationErrorCode.REQUIRE_LOGIN);
    }
}
