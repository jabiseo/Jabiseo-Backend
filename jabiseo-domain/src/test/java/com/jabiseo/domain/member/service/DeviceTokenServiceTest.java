package com.jabiseo.domain.member.service;

import com.jabiseo.domain.member.domain.DeviceToken;
import com.jabiseo.domain.member.repository.DeviceTokenRepository;
import com.jabiseo.domain.member.domain.Member;
import fixture.MemberFixture;
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
@DisplayName("deviceTokenService 테스트")
class DeviceTokenServiceTest {

    @InjectMocks
    DeviceTokenService deviceTokenService;

    @Mock
    DeviceTokenRepository deviceTokenRepository;

    Member member = MemberFixture.createMember(1L);
    String deviceId = "1";

    @Test
    @DisplayName("login token 요청시 token 값이 null이면 수행하지 않는다.")
    void tokenIsNullNotAction() {
        //given
        String token = null;

        //when
        deviceTokenService.loginToken(member, token, deviceId);

        //then
        verify(deviceTokenRepository, times(0)).findByMemberAndDeviceId(member, deviceId);
    }

    @Test
    @DisplayName("loginToken 요청시 값이 저장되있지 않다면 새 값을 생성해 저장한다.")
    void isNotSaveTokenThenCreateNewToken() {
        //given
        String token = "token";
        DeviceToken deviceToken = spy(DeviceToken.create(deviceId, token, member));
        given(deviceTokenRepository.findByMemberAndDeviceId(member, deviceId)).willReturn(Optional.of(deviceToken));
        //when
        deviceTokenService.loginToken(member, token, deviceId);

        //then
        verify(deviceTokenRepository, times(1)).findByMemberAndDeviceId(member, deviceId);
        verify(deviceToken, times(1)).updateToken(token);
    }
}
