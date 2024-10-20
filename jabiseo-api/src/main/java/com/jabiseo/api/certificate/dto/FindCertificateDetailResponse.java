package com.jabiseo.api.certificate.dto;

import com.jabiseo.domain.certificate.domain.Certificate;

import java.util.List;

public record FindCertificateDetailResponse(
        Long certificateId,
        String name,
        List<ExamResponse> exams,
        List<SubjectResponse> subjects
) {
    public static FindCertificateDetailResponse from(Certificate certificate) {
        return new FindCertificateDetailResponse(
                certificate.getId(),
                certificate.getName(),
                certificate.getExams()
                        .stream()
                        .map(ExamResponse::from)
                        .toList(),
                certificate.getSubjects()
                        .stream()
                        .map(SubjectResponse::from)
                        .toList()
        );
    }
}
