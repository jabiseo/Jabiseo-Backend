package com.jabiseo.api.auth.application.usecase;

import com.jabiseo.api.auth.application.JwtHandler;
import com.jabiseo.api.auth.application.oidc.TokenValidatorManager;
import com.jabiseo.api.auth.dto.LoginRequest;
import com.jabiseo.api.auth.dto.LoginResponse;
import com.jabiseo.domain.auth.domain.Auth;
import com.jabiseo.domain.auth.domain.AuthService;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.OauthMemberInfo;
import com.jabiseo.domain.member.domain.OauthServer;
import com.jabiseo.domain.member.service.DeviceTokenService;
import com.jabiseo.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static fixture.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("login usecase 테스트")
@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @InjectMocks
    LoginUseCase loginUseCase;

    @Mock
    TokenValidatorManager tokenValidatorManager;

    @Mock
    JwtHandler jwtHandler;

    @Mock
    AuthService authService;

    @Mock
    MemberService memberService;
    @Mock
    DeviceTokenService deviceTokenService;


    @Test
    @DisplayName("로그인 이후 Jwt를 발급 및 저장한다.")
    void loginSuccessCreateJwtAndSave() throws Exception {
        //given
        LoginRequest request = new LoginRequest("idToken", "KAKAO", "token");
        OauthMemberInfo memberInfo = new OauthMemberInfo("id", OauthServer.KAKAO, "email@emil.com");
        Member member = createMember(1L);
        String access = "access";
        String refresh = "refresh";
        String deviceId = "123";
        given(tokenValidatorManager.validate(request.idToken(), OauthServer.valueOf(request.oauthServer()))).willReturn(memberInfo);
        given(memberService.getByOauthIdAndOauthServerOrCreateMember(memberInfo)).willReturn(member);
        given(jwtHandler.createAccessToken(member)).willReturn(access);
        given(jwtHandler.createRefreshToken()).willReturn(refresh);

        //when
        LoginResponse result = loginUseCase.execute(request, deviceId);

        //then
        assertThat(result.accessToken()).isEqualTo(access);
        assertThat(result.refreshToken()).isEqualTo(refresh);
        verify(authService, times(1)).login(Auth.create(deviceId, member.getId(), refresh));
        verify(deviceTokenService, times(1)).loginToken(member, request.fcmToken(), deviceId);
    }
}
