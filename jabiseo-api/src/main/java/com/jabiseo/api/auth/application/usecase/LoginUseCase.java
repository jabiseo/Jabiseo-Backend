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
import com.jabiseo.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginUseCase {

    private final TokenValidatorManager tokenValidatorManager;
    private final JwtHandler jwtHandler;
    private final MemberService memberService;
    private final AuthService authService;

    public LoginResponse execute(LoginRequest loginRequest, String deviceId) {
        OauthMemberInfo oauthMemberInfo = tokenValidatorManager.validate(loginRequest.idToken(), OauthServer.valueOf(loginRequest.oauthServer()));

        Member member = memberService.getByOauthIdAndOauthServerOrCreateMember(oauthMemberInfo);

        String accessToken = jwtHandler.createAccessToken(member);
        String refreshToken = jwtHandler.createRefreshToken();
        authService.login(Auth.create(deviceId, member.getId(), refreshToken));

        return new LoginResponse(accessToken, refreshToken);
    }

}
