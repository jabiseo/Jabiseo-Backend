package com.jabiseo.api.certificate.dto;


import com.jabiseo.domain.certificate.domain.Certificate;

public record FindCertificateListResponse(
        Long certificateId,
        String name
) {
    public static FindCertificateListResponse from(Certificate certificate) {
        return new FindCertificateListResponse(certificate.getId(), certificate.getName());
    }
}
