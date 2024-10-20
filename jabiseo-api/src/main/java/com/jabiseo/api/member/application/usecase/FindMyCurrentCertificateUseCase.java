package com.jabiseo.api.member.application.usecase;

import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.api.member.dto.FindMyCurrentCertificateResponse;
import com.jabiseo.domain.member.exception.MemberBusinessException;
import com.jabiseo.domain.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindMyCurrentCertificateUseCase {

    private final MemberRepository memberRepository;

    public FindMyCurrentCertificateResponse execute(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.getCurrentCertificate() == null) {
            return FindMyCurrentCertificateResponse.from(member);
        } else {
            return FindMyCurrentCertificateResponse.of(member, member.getCurrentCertificate());
        }
    }
}
