package com.jabiseo.api.member.application.usecase;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.domain.CertificateRepository;
import com.jabiseo.domain.certificate.exception.CertificateErrorCode;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.api.member.dto.UpdateMyCurrentCertificateRequest;
import com.jabiseo.domain.member.exception.MemberBusinessException;
import com.jabiseo.domain.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateMyCurrentCertificateUseCase {

    private final MemberRepository memberRepository;
    private final CertificateRepository certificateRepository;

    public void execute(Long memberId, UpdateMyCurrentCertificateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
        Certificate certificate = certificateRepository.findById(request.certificateId())
                .orElseThrow(() -> new MemberBusinessException(CertificateErrorCode.CERTIFICATE_NOT_FOUND));
        member.updateCurrentCertificate(certificate);
    }
}
