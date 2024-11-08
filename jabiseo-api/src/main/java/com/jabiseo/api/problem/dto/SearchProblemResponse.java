package com.jabiseo.api.problem.dto;

import com.jabiseo.api.certificate.dto.ExamResponse;
import com.jabiseo.api.certificate.dto.SubjectResponse;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkSummaryScoreQueryDto;

public record SearchProblemResponse(
        Long problemId,
        Double score,
        ExamResponse examInfo,
        SubjectResponse subjectInfo,
        boolean isBookmark,
        String description
) {
    public static SearchProblemResponse from(ProblemWithBookmarkSummaryScoreQueryDto dto) {
        return new SearchProblemResponse(
                dto.problemId(),
                dto.score(),
                ExamResponse.of(dto.examId(), dto.examDescription()),
                SubjectResponse.of(dto.subjectId(), dto.subjectSequence(), dto.subjectName()),
                dto.isBookmark(),
                dto.description()
        );
    }
}
