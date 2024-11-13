package com.jabiseo.api.auth.application.usecase;

import com.jabiseo.domain.auth.domain.Auth;
import com.jabiseo.domain.auth.domain.AuthService;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.DeviceTokenService;
import com.jabiseo.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutUseCase {

    private final AuthService authService;
    private final DeviceTokenService deviceTokenService;
    private final MemberService memberService;

    public void execute(Long memberId, String deviceId) {
        authService.logout(Auth.create(deviceId, memberId, null));

        Member member = memberService.getById(memberId);
        deviceTokenService.deleteToken(member, deviceId);
    }

}
