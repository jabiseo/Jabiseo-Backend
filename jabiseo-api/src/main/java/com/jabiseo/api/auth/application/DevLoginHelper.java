package com.jabiseo.api.auth.application;


import com.jabiseo.api.auth.dto.LoginResponse;
import com.jabiseo.domain.auth.domain.Auth;
import com.jabiseo.domain.auth.domain.AuthService;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.DeviceTokenService;
import com.jabiseo.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DevLoginHelper {

    private final JwtHandler jwtHandler;
    private final MemberService memberService;
    private final AuthService authService;
    private final DeviceTokenService deviceTokenService;

    public LoginResponse login(Long memberId, String deviceId, String token) {
        Member member = memberService.getById(memberId);

        String accessToken = jwtHandler.createAccessToken(member);
        String refreshToken = jwtHandler.createRefreshToken();
        authService.login(Auth.create(deviceId, memberId, refreshToken));
        deviceTokenService.loginToken(member, token, deviceId);
        return new LoginResponse(accessToken, refreshToken);
    }

}
