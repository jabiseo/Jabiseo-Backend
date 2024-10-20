package com.jabiseo.api.member.dto;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;

public record FindMyCurrentCertificateResponse(
        String memberId,
        Long certificateId
) {
    public static FindMyCurrentCertificateResponse of(Member member, Certificate certificate) {
        return new FindMyCurrentCertificateResponse(member.getId().toString(), certificate.getId());
    }

    public static FindMyCurrentCertificateResponse from(Member member) {
        return new FindMyCurrentCertificateResponse(member.getId().toString(), null);
    }
}
