package com.jabiseo.api.certificate.application.usecase;

import com.jabiseo.api.certificate.dto.FindCertificateDetailResponse;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindCertificateDetailUseCase {

    private final CertificateService certificateService;

    public FindCertificateDetailResponse execute(Long certificateId) {
        Certificate certificate = certificateService.getByIdWithExamsAndSubjects(certificateId);
        return FindCertificateDetailResponse.from(certificate);
    }
}
