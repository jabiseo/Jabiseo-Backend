package com.jabiseo.domain.auth.domain;

import com.jabiseo.domain.auth.exception.AuthenticationBusinessException;
import com.jabiseo.domain.auth.exception.AuthenticationErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    AuthRepository authRepository;

    Auth requestAuth;

    @BeforeEach
    void setUp() {
        String requestDeviceId = "deviceId";
        Long requestMemberId = 1L;
        String requestTokenValue = "tokens";
        requestAuth = Auth.create(requestDeviceId, requestMemberId, requestTokenValue);
    }

    @Test
    @DisplayName("로그인 성공시 인증 정보를 저장한다.")
    void loginSuccess() {
        //given
        //when
        authService.login(requestAuth);

        //then
        verify(authRepository, times(1)).saveAuth(requestAuth);
    }

    @Test
    @DisplayName("재발급 요청시 저장된 인증 정보가 없다면 예외를 반환한다.")
    void isNotSaveAuthThrowException() {
        //given
        given(authRepository.getSavedAuth(requestAuth)).willReturn(Optional.empty());

        // when then
        Assertions.assertThatThrownBy(() -> authService.reissue(requestAuth))
                .isInstanceOf(AuthenticationBusinessException.class)
                .hasMessageContaining(AuthenticationErrorCode.REQUIRE_LOGIN.getMessage());
    }


    @Test
    @DisplayName("재발급 요청시 저장된 인증 정보가 있으면 확인을 수행한다.")
    void isSaveAuthCallingCheckMethod() {
        //given
        Auth savedAuth = spy(Auth.create(requestAuth.getDeviceId(), requestAuth.getMemberId(), requestAuth.getTokenValue()));

        given(authRepository.getSavedAuth(requestAuth)).willReturn(Optional.of(savedAuth));

        // when
        authService.reissue(requestAuth);

        //then
        verify(savedAuth, times(1)).checkAuthToken(requestAuth.getTokenValue());
    }

    @Test
    @DisplayName("로그아웃 요청시 저장된 인증정보를 삭제한다.")
    void logoutSuccessCallDeleteMethod(){
        //given
        //when
        authService.logout(requestAuth);

        //then
        verify(authRepository, times(1)).deleteAuth(requestAuth.getAuthKey());
    }


}
