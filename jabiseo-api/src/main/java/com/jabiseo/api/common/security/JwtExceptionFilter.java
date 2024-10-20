package com.jabiseo.api.common.security;

import com.jabiseo.api.common.ResponseFactory;
import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.CommonErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (BusinessException e) {
            log.error(e.getErrorCode().getMessage());
            ResponseFactory.fail(response, e.getErrorCode());
        } catch (Exception e) {
            log.error(e.getMessage());
            ResponseFactory.fail(response, CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
