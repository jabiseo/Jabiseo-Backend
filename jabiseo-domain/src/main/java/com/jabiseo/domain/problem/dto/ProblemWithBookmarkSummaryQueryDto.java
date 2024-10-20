package com.jabiseo.domain.problem.dto;

public record ProblemWithBookmarkSummaryQueryDto(
        Long problemId,
        String description,
        boolean isBookmark,

        Long examId,
        String examDescription,

        Long subjectId,
        String subjectName,
        int subjectSequence
) {
}
