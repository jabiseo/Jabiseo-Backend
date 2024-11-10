package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.FindProblemDetailResponse;
import com.jabiseo.api.problem.dto.ProblemsDetailResponse;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkDetailQueryDto;
import com.jabiseo.domain.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindProblemDetailUseCase {

    private final ProblemService problemService;

    public FindProblemDetailResponse execute(Long memberId, Long problemId) {
        ProblemWithBookmarkDetailQueryDto dto = problemService.findDetailByIdWithBookmark(memberId, problemId);
        ProblemsDetailResponse problemDetailResponse = ProblemsDetailResponse.from(dto);

        return FindProblemDetailResponse.of(problemDetailResponse);
    }
}
