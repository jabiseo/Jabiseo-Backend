package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.response.SearchProblemResponse;
import com.jabiseo.domain.problem.service.ProblemService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchProblemUseCase {

    private final ProblemService problemService;

    public List<SearchProblemResponse> execute(Long memberId, Long certificateId, String query,
                                               @Nullable Double lastScore, @Nullable Long lastId) {
        return problemService.searchProblem(memberId, certificateId, query, lastScore, lastId).stream()
                .map(SearchProblemResponse::from)
                .toList();
    }
}
