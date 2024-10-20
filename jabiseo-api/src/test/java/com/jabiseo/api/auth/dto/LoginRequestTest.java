package com.jabiseo.api.auth.dto;

import com.jabiseo.api.auth.dto.LoginRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("loginRequest 입력값 검증 테스트")
class LoginRequestTest {

    private ValidatorFactory validatorFactory;
    private Validator validator;

    @BeforeEach
    void init() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }


    @Test
    @DisplayName("login 요청시 null값이 오면 예외를 반환한다.")
    void nullRequestThrownException() {
        //given
        String idToken = null;
        String oauthServer = null;
        LoginRequest loginRequest = new LoginRequest(idToken, oauthServer);

        //when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        //then
        assertThat(violations).isNotEmpty();
    }

    @DisplayName("login 요청시 oauthServer에 정확한 값이 오지 않으면 예외를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"kakao", "kakakkao", "", "value", "ggogo", "KAKAOOO"})
    void oauthServerNotAllowInputsThrowException(String oauthServer) {
        String idToken = "IdTokens..";
        LoginRequest loginRequest = new LoginRequest(idToken, oauthServer);

        //when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        //then
        assertThat(violations).isNotEmpty();
    }

    @DisplayName("정상적인 입력 요청시 성공한다")
    @ParameterizedTest
    @ValueSource(strings = {"KAKAO", "GOOGLE"})
    void LoginRequestSuccess(String oauthServer) {
        //given
        String idToken = "IdTokens..";
        LoginRequest loginRequest = new LoginRequest(idToken, oauthServer);

        //when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        //then
        assertThat(violations).isEmpty();
    }

}
