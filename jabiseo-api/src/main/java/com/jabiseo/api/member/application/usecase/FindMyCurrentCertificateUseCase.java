package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.dto.FindMyCurrentCertificateResponse;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindMyCurrentCertificateUseCase {

    private final MemberService memberService;

    public FindMyCurrentCertificateResponse execute(Long memberId) {
        Member member = memberService.getByIdWithCertificate(memberId);

        if (member.getCurrentCertificate() == null) {
            return FindMyCurrentCertificateResponse.from(member);
        } else {
            return FindMyCurrentCertificateResponse.of(member, member.getCurrentCertificate());
        }
    }
}
