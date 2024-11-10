package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.FindSimilarProblemResponse;
import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkSummaryQueryDto;
import com.jabiseo.domain.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindSimilarProblemsUseCase {

    private final ProblemService problemService;

    public List<FindSimilarProblemResponse> execute(Long memberId, Long problemId) {

        Problem problem = problemService.getById(problemId);
        Long certificateId = problem.getCertificate().getId();

        List<ProblemWithBookmarkSummaryQueryDto> dtos = problemService.findSimilarProblems(memberId, problemId, certificateId);

        return dtos.stream()
                .map(FindSimilarProblemResponse::of)
                .toList();
    }
}
