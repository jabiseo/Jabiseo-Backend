package com.jabiseo.api.auth.application.usecase;

import com.jabiseo.api.auth.application.JwtHandler;
import com.jabiseo.api.auth.application.MemberFactory;
import com.jabiseo.api.auth.application.oidc.OauthMemberInfo;
import com.jabiseo.api.auth.application.oidc.TokenValidatorManager;
import com.jabiseo.api.auth.dto.LoginRequest;
import com.jabiseo.api.auth.dto.LoginResponse;
import com.jabiseo.infra.cache.RedisCacheRepository;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.domain.member.domain.OauthServer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@Transactional
@RequiredArgsConstructor
public class LoginUseCase {

    private final TokenValidatorManager tokenValidatorManager;
    private final MemberFactory memberFactory;
    private final JwtHandler jwtHandler;
    private final MemberRepository memberRepository;
    private final RedisCacheRepository cacheRepository;

    public LoginResponse execute(LoginRequest loginRequest) {
        OauthMemberInfo oauthMemberInfo = tokenValidatorManager.validate(loginRequest.idToken(), OauthServer.valueOf(loginRequest.oauthServer()));

        Member member = memberRepository.findByOauthIdAndOauthServer(oauthMemberInfo.getOauthId(), oauthMemberInfo.getOauthServer())
                .orElseGet(() -> {
                    Member newMember = memberFactory.createNew(oauthMemberInfo);
                    return memberRepository.save(newMember);
                });


        String accessToken = jwtHandler.createAccessToken(member);
        String refreshToken = jwtHandler.createRefreshToken();
        cacheRepository.saveToken(member.getId(), refreshToken);

        return new LoginResponse(accessToken, refreshToken);
    }

}
