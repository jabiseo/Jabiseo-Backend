package com.jabiseo.api.auth.application.usecase;

import com.jabiseo.api.auth.application.JwtHandler;
import com.jabiseo.api.auth.application.MemberFactory;
import com.jabiseo.api.auth.application.oidc.OauthMemberInfo;
import com.jabiseo.api.auth.application.oidc.TokenValidatorManager;
import com.jabiseo.api.auth.application.usecase.LoginUseCase;
import com.jabiseo.api.auth.dto.LoginRequest;
import com.jabiseo.api.auth.dto.LoginResponse;
import com.jabiseo.infra.cache.RedisCacheRepository;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.domain.member.domain.OauthServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static fixture.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    MemberFactory memberFactory;

    @Mock
    JwtHandler jwtHandler;

    @Mock
    MemberRepository memberRepository;

    @Mock
    RedisCacheRepository redisCacheRepository;


    @Test
    @DisplayName("처음 요청 오는 OAuth 회원일시 맴버 객체를 생성하고 저장한다.")
    void firstOauthUserIsSignUpAndSave() {
        //given
        LoginRequest request = new LoginRequest("idToken", "KAKAO");
        OauthMemberInfo memberInfo = new OauthMemberInfo("id", OauthServer.KAKAO, "email@emil.com");
        Member member = createMember(1L);
        given(memberRepository.findByOauthIdAndOauthServer(memberInfo.getOauthId(), memberInfo.getOauthServer())).willReturn(Optional.empty());
        given(tokenValidatorManager.validate(request.idToken(), OauthServer.valueOf(request.oauthServer()))).willReturn(memberInfo);
        given(memberFactory.createNew(memberInfo)).willReturn(member);
        given(memberRepository.save(any())).willReturn(member);

        //when
        loginUseCase.execute(request);

        //then
        verify(memberFactory, times(1)).createNew(memberInfo);
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    @DisplayName("로그인 이후 Jwt를 발급 및 저장한다.")
    void loginSuccessCreateJwtAndSave() throws Exception {
        //given
        LoginRequest request = new LoginRequest("idToken", "KAKAO");
        OauthMemberInfo memberInfo = new OauthMemberInfo("id", OauthServer.KAKAO, "email@emil.com");
        Member member = createMember(1L);
        String access = "access";
        String refresh = "refresh";
        given(memberRepository.findByOauthIdAndOauthServer(memberInfo.getOauthId(), memberInfo.getOauthServer())).willReturn(Optional.of(member));
        given(tokenValidatorManager.validate(request.idToken(), OauthServer.valueOf(request.oauthServer()))).willReturn(memberInfo);
        given(jwtHandler.createAccessToken(member)).willReturn(access);
        given(jwtHandler.createRefreshToken()).willReturn(refresh);

        //when
        LoginResponse result = loginUseCase.execute(request);

        //then
        assertThat(result.accessToken()).isEqualTo(access);
        assertThat(result.refreshToken()).isEqualTo(refresh);
        verify(redisCacheRepository, times(1)).saveToken(member.getId(), refresh);
    }
}
