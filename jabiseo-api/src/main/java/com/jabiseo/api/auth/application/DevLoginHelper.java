package com.jabiseo.api.auth.application;


import com.jabiseo.api.auth.dto.LoginResponse;
import com.jabiseo.infra.cache.RedisCacheRepository;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.domain.member.exception.MemberBusinessException;
import com.jabiseo.domain.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DevLoginHelper {

    private final JwtHandler jwtHandler;
    private final MemberRepository memberRepository;
    private final RedisCacheRepository redisCacheRepository;

    public LoginResponse login(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        String accessToken = jwtHandler.createAccessToken(member);
        String refreshToken = jwtHandler.createRefreshToken();
        redisCacheRepository.saveToken(member.getId(), refreshToken);
        return new LoginResponse(accessToken, refreshToken);
    }

}
