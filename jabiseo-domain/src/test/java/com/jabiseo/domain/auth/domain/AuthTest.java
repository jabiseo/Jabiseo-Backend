package com.jabiseo.domain.auth.domain;

import com.jabiseo.domain.auth.exception.AuthenticationBusinessException;
import com.jabiseo.domain.auth.exception.AuthenticationErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Auth 엔티티 테스트")
class AuthTest {


    @Test
    @DisplayName("key 요청시 memberId와 deviceId가 포함된 올바른 Key값을 리턴한다")
    void getKeySuccess(){
        //given
        Long memberId = 1L;
        String deviceId = "deviceId";
        String token = "tokens..";
        Auth auth = Auth.create(deviceId, memberId, token);

        //when
        String expectedAuthKey = "AUTH_1_deviceId";
        String authKey = auth.getAuthKey();

        //then
        assertThat(authKey).isEqualTo(expectedAuthKey);
    }

    @Test
    @DisplayName("Auth 객체의 TokenValue 값과 일치하지 않는 경우 예외를 반환한다.")
    void checkAuthTokenFailThrownException(){
        //given
        Long memberId = 1L;
        String deviceId = "deviceId";
        String token = "tokens..";
        Auth auth = Auth.create(deviceId, memberId, token);

        //when
        String notMatchToken = "Token";

        //then
        assertThatThrownBy(()-> auth.checkAuthToken(notMatchToken))
                .isInstanceOf(AuthenticationBusinessException.class)
                .hasMessage(AuthenticationErrorCode.NOT_MATCH_REFRESH.getMessage());
    }
}
