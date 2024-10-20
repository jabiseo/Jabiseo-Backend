package com.jabiseo.api.certificate.dto;

import com.jabiseo.domain.certificate.domain.Exam;

public record ExamResponse(
        Long examId,
        String description
) {
    public static ExamResponse from(Exam exam) {
        return new ExamResponse(exam.getId(), exam.getDescription());
    }

    public static ExamResponse of(Long examId, String description) {
        return new ExamResponse(examId, description);
    }
}
