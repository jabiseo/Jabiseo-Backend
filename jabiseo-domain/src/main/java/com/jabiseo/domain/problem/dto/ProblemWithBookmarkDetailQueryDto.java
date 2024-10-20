package com.jabiseo.domain.problem.dto;

public record ProblemWithBookmarkDetailQueryDto(
    Long problemId,
    String description,
    String choice1,
    String choice2,
    String choice3,
    String choice4,
    int answerNumber,
    String solution,
    boolean isBookmark,

    Long examId,
    String examDescription,

    Long subjectId,
    String subjectName,
    int subjectSequence
) {
}
