package com.jabiseo.api.config.auth;

import com.jabiseo.domain.auth.exception.AuthenticationBusinessException;
import com.jabiseo.domain.auth.exception.AuthenticationErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthenticatedMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean isAuthenticatedMemberAnnotation = parameter.getParameterAnnotation(AuthenticatedMember.class) != null;
        boolean isAuthMemberClass = AuthMember.class.equals(parameter.getParameterType());
        return isAuthenticatedMemberAnnotation && isAuthMemberClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AuthenticationBusinessException(AuthenticationErrorCode.REQUIRE_LOGIN);
        }

        if (authentication instanceof AnonymousAuthenticationToken) {
            return new AuthMember(null);
        } else {
            return new AuthMember(Long.parseLong(authentication.getPrincipal().toString()));
        }
    }
}
