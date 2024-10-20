package com.jabiseo.api.problem.dto;

import com.jabiseo.domain.certificate.domain.Certificate;

public record CertificateResponse(
        Long certificateId,
        String name
) {
    public static CertificateResponse from(Certificate certificate) {
        return new CertificateResponse(
                certificate.getId(),
                certificate.getName()
        );
    }
}
