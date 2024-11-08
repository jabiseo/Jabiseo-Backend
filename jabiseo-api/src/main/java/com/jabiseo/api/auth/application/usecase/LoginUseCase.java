package com.jabiseo.api.auth.application.usecase;

import com.jabiseo.api.auth.application.JwtHandler;
import com.jabiseo.api.auth.application.oidc.TokenValidatorManager;
import com.jabiseo.api.auth.dto.LoginRequest;
import com.jabiseo.api.auth.dto.LoginResponse;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.OauthMemberInfo;
import com.jabiseo.domain.member.domain.OauthServer;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.infra.cache.RedisCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final TokenValidatorManager tokenValidatorManager;
    private final JwtHandler jwtHandler;
    private final MemberService memberService;
    private final RedisCacheRepository cacheRepository;

    public LoginResponse execute(LoginRequest loginRequest) {
        OauthMemberInfo oauthMemberInfo = tokenValidatorManager.validate(loginRequest.idToken(), OauthServer.valueOf(loginRequest.oauthServer()));

        Member member = memberService.getByOauthIdAndOauthServerOrCreateMember(oauthMemberInfo);

        String accessToken = jwtHandler.createAccessToken(member);
        String refreshToken = jwtHandler.createRefreshToken();
        cacheRepository.saveToken(member.getId(), refreshToken);

        return new LoginResponse(accessToken, refreshToken);
    }

}
