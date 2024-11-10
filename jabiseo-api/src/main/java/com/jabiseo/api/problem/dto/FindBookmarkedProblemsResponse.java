package com.jabiseo.api.problem.dto;

import com.jabiseo.domain.problem.dto.ProblemWithBookmarkSummaryQueryPageDto;

import java.util.List;

public record FindBookmarkedProblemsResponse(
        long totalCount,
        long totalPage,
        List<ProblemsSummaryResponse> problems
) {
    public static FindBookmarkedProblemsResponse from(ProblemWithBookmarkSummaryQueryPageDto dto) {
        return new FindBookmarkedProblemsResponse(
                dto.totalCount(),
                dto.totalPage(),
                dto.problems().stream()
                        .map(ProblemsSummaryResponse::from)
                        .toList()
        );
    }
}
