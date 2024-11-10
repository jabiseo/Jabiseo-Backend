package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.dto.UpdateMyCurrentCertificateRequest;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.service.CertificateService;
import com.jabiseo.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateMyCurrentCertificateUseCase {

    private final MemberService memberService;
    private final CertificateService certificateService;

    public void execute(Long memberId, UpdateMyCurrentCertificateRequest request) {
        Certificate certificate = certificateService.getById(request.certificateId());
        memberService.updateCurrentCertificate(memberId, certificate);
    }
}
