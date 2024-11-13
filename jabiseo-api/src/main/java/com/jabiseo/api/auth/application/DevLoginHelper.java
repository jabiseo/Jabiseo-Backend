package com.jabiseo.api.auth.application;


import com.jabiseo.api.auth.dto.LoginResponse;
import com.jabiseo.api.config.deviceinfo.DeviceInfo;
import com.jabiseo.domain.auth.domain.Auth;
import com.jabiseo.domain.auth.domain.AuthService;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.infra.cache.RedisCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DevLoginHelper {

    private final JwtHandler jwtHandler;
    private final MemberService memberService;
    private final AuthService authService;
    private final RedisCacheRepository redisCacheRepository;

    public LoginResponse login(Long memberId, String deviceId) {
        Member member = memberService.getById(memberId);

        String accessToken = jwtHandler.createAccessToken(member);
        String refreshToken = jwtHandler.createRefreshToken();
        authService.login(Auth.create(deviceId, memberId, refreshToken));
        return new LoginResponse(accessToken, refreshToken);
    }

}
