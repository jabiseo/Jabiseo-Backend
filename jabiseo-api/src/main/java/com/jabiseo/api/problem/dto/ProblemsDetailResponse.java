package com.jabiseo.api.problem.dto;

import com.jabiseo.api.certificate.dto.ExamResponse;
import com.jabiseo.api.certificate.dto.SubjectResponse;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkDetailQueryDto;

import java.util.List;

public record ProblemsDetailResponse(
        Long problemId,
        ExamResponse examInfo,
        SubjectResponse subjectInfo,
        boolean isBookmark,
        String description,
        List<ChoiceResponse> choices,
        int answerNumber,
        String solution
) {
    public static ProblemsDetailResponse from(ProblemWithBookmarkDetailQueryDto bookmarkedProblemDetailDto) {
        return new ProblemsDetailResponse(
                bookmarkedProblemDetailDto.problemId(),
                ExamResponse.of(
                        bookmarkedProblemDetailDto.examId(),
                        bookmarkedProblemDetailDto.examDescription()
                ),
                SubjectResponse.of(
                        bookmarkedProblemDetailDto.subjectId(),
                        bookmarkedProblemDetailDto.subjectSequence(),
                        bookmarkedProblemDetailDto.subjectName()
                ),
                bookmarkedProblemDetailDto.isBookmark(),
                bookmarkedProblemDetailDto.description(),
                ChoiceResponse.fromChoices(List.of(
                                bookmarkedProblemDetailDto.choice1(),
                                bookmarkedProblemDetailDto.choice2(),
                                bookmarkedProblemDetailDto.choice3(),
                                bookmarkedProblemDetailDto.choice4()
                        )
                ),
                bookmarkedProblemDetailDto.answerNumber(),
                bookmarkedProblemDetailDto.solution()
        );
    }
}
