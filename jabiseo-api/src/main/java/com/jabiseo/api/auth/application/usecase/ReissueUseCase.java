package com.jabiseo.api.auth.application.usecase;

import com.jabiseo.api.auth.application.JwtHandler;
import com.jabiseo.api.auth.dto.ReissueRequest;
import com.jabiseo.api.auth.dto.ReissueResponse;
import com.jabiseo.domain.auth.domain.Auth;
import com.jabiseo.domain.auth.domain.AuthService;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReissueUseCase {

    private final MemberService memberService;
    private final AuthService authService;
    private final JwtHandler jwtHandler;

    public ReissueResponse execute(ReissueRequest request, Long memberId, String deviceId) {
        Member member = memberService.getById(memberId);

        jwtHandler.validateRefreshToken(request.refreshToken());
        authService.reissue(Auth.create(deviceId, member.getId(), request.refreshToken()));

        String accessToken = jwtHandler.createAccessToken(member);
        return new ReissueResponse(accessToken);
    }


}
