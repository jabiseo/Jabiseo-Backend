package com.jabiseo.api.problem.dto.response;

import com.jabiseo.api.certificate.dto.ExamResponse;
import com.jabiseo.api.certificate.dto.SubjectResponse;

import java.util.List;

public record FindProblemDetailResponse(
        Long problemId,
        ExamResponse examInfo,
        SubjectResponse subjectInfo,
        boolean isBookmark,
        String description,
        List<ChoiceResponse> choices,
        int answerNumber,
        String solution
) {
    public static FindProblemDetailResponse of(ProblemsDetailResponse problemsDetailResponse) {
        return new FindProblemDetailResponse(
                problemsDetailResponse.problemId(),
                problemsDetailResponse.examInfo(),
                problemsDetailResponse.subjectInfo(),
                problemsDetailResponse.isBookmark(),
                problemsDetailResponse.description(),
                problemsDetailResponse.choices(),
                problemsDetailResponse.answerNumber(),
                problemsDetailResponse.solution()
        );
    }
}
