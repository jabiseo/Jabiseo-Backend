package com.jabiseo.api.problem.dto;

import com.jabiseo.api.certificate.dto.ExamResponse;
import com.jabiseo.api.certificate.dto.SubjectResponse;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkSummaryQueryDto;

public record ProblemsSummaryResponse(
        Long problemId,
        ExamResponse examInfo,
        SubjectResponse subjectInfo,
        boolean isBookmark,
        String description
) {
    public static ProblemsSummaryResponse from(ProblemWithBookmarkSummaryQueryDto dto) {
        return new ProblemsSummaryResponse(
                dto.problemId(),
                ExamResponse.of(dto.examId(), dto.examDescription()),
                SubjectResponse.of(dto.subjectId(), dto.subjectSequence(), dto.subjectName()),
                dto.isBookmark(),
                dto.description()
        );
    }
}
