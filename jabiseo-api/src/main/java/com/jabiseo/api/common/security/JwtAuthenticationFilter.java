package com.jabiseo.api.common.security;

import com.jabiseo.api.auth.application.JwtHandler;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtHandler jwtHandler;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String HEADER_PREFIX = "Bearer ";
    private static final String REISSUE_REQUEST = "/api/auth/reissue";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            Claims claims = getClaimsFromToken(token, request);
            setAuthenticationToContext(claims);
        }

        filterChain.doFilter(request, response);
    }

    private Claims getClaimsFromToken(String token, HttpServletRequest request) {
        if (request.getRequestURI().equals(REISSUE_REQUEST)) {
            jwtHandler.validateAccessTokenNotCheckExpired(token);
        } else {
            jwtHandler.validateAccessToken(token);
        }
        return jwtHandler.getClaimsFromAccessToken(token);
    }

    private void setAuthenticationToContext(Claims jwtClaim) {
        String memberId = jwtClaim.getSubject();
        Set<GrantedAuthority> authorities = new HashSet<>();
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberId, "",
                authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HEADER_PREFIX)) {
            return bearerToken.substring(HEADER_PREFIX.length());
        }
        return null;
    }
}
