package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.FindProblemDetailResponse;
import com.jabiseo.api.problem.dto.ProblemsDetailResponse;
import com.jabiseo.domain.problem.domain.ProblemRepository;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkDetailQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindProblemDetailUseCase {

    private final ProblemRepository problemRepository;

    public FindProblemDetailResponse execute(Long memberId, Long problemId) {

        ProblemWithBookmarkDetailQueryDto dto = problemRepository.findDetailByIdWithBookmark(memberId, problemId);
        ProblemsDetailResponse problemDetailResponse = ProblemsDetailResponse.from(dto);

        return FindProblemDetailResponse.of(problemDetailResponse);
    }
}
