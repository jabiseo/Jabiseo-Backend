package com.jabiseo.api.auth.application.usecase;

import com.jabiseo.api.auth.application.JwtHandler;
import com.jabiseo.api.auth.dto.ReissueRequest;
import com.jabiseo.api.auth.dto.ReissueResponse;
import com.jabiseo.domain.auth.exception.AuthenticationBusinessException;
import com.jabiseo.domain.auth.exception.AuthenticationErrorCode;
import com.jabiseo.infra.cache.RedisCacheRepository;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReissueUseCase {

    private final MemberRepository memberRepository;
    private final RedisCacheRepository redisCacheRepository;
    private final JwtHandler jwtHandler;

    public ReissueResponse execute(ReissueRequest request, Long memberId) {
        Member member = memberRepository.getReferenceById(memberId);

        jwtHandler.validateRefreshToken(request.refreshToken());

        String savedToken = redisCacheRepository.findToken(memberId)
                .orElseThrow(() -> new AuthenticationBusinessException(AuthenticationErrorCode.REQUIRE_LOGIN));

        if (!savedToken.equals(request.refreshToken())) {
            throw new AuthenticationBusinessException(AuthenticationErrorCode.NOT_MATCH_REFRESH);
        }

        String accessToken = jwtHandler.createAccessToken(member);
        return new ReissueResponse(accessToken);
    }


}
