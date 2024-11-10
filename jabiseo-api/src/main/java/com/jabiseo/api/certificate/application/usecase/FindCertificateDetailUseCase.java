package com.jabiseo.api.certificate.application.usecase;

import com.jabiseo.api.certificate.dto.FindCertificateDetailResponse;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindCertificateDetailUseCase {

    private final CertificateService certificateService;

    public FindCertificateDetailResponse execute(Long certificateId) {
        Certificate certificate = certificateService.getByIdWithExamsAndSubjects(certificateId);
        return FindCertificateDetailResponse.from(certificate);
    }
}
