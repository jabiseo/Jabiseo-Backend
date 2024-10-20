package com.jabiseo.domain.problem.dto;

public record ProblemWithBookmarkSummaryScoreQueryDto(
        Long problemId,
        Double score,
        String description,
        boolean isBookmark,

        Long examId,
        String examDescription,

        Long subjectId,
        String subjectName,
        int subjectSequence
) {
    public static ProblemWithBookmarkSummaryScoreQueryDto of(ProblemWithBookmarkSummaryQueryDto dto, Double score) {
        return new ProblemWithBookmarkSummaryScoreQueryDto(
                dto.problemId(),
                score,
                dto.description(),
                dto.isBookmark(),
                dto.examId(),
                dto.examDescription(),
                dto.subjectId(),
                dto.subjectName(),
                dto.subjectSequence()
        );
    }
}
