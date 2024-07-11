package com.jabiseo.auth.application.usecase;

import com.jabiseo.auth.application.MemberFactory;
import com.jabiseo.auth.domain.TokenRepository;
import com.jabiseo.auth.dto.LoginRequest;
import com.jabiseo.auth.dto.LoginResponse;
import com.jabiseo.auth.application.JwtHandler;
import com.jabiseo.auth.application.oidc.OauthMemberInfo;
import com.jabiseo.auth.application.oidc.TokenValidatorManager;
import com.jabiseo.member.domain.Member;
import com.jabiseo.member.domain.MemberRepository;
import com.jabiseo.member.domain.OauthServer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final TokenValidatorManager tokenValidatorManager;
    private final MemberFactory memberFactory;
    private final JwtHandler jwtHandler;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    public LoginResponse execute(LoginRequest loginRequest) {
        OauthMemberInfo oauthMemberInfo = tokenValidatorManager.validate(loginRequest.idToken(), loginRequest.oauthServer());

        String oauthId = oauthMemberInfo.getOauthId();
        OauthServer oauthServer = oauthMemberInfo.getOauthServer();

        Member member = memberRepository.findByOauthIdAndOauthServer(oauthId, oauthServer)
                .orElse(null);

        if (isRequireSignup(member)) {
            Member newMember = memberFactory.createNew(oauthId, oauthServer);
            member = memberRepository.save(newMember);
        }


        String accessToken = jwtHandler.createAccessToken(member);
        String refreshToken = jwtHandler.createRefreshToken();
        tokenRepository.saveToken(member.getId(), refreshToken);

        return new LoginResponse(accessToken, refreshToken);
    }


    private static boolean isRequireSignup(Member member) {
        return member == null;
    }
}
