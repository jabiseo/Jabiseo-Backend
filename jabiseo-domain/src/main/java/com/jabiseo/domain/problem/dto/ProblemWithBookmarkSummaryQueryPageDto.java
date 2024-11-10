package com.jabiseo.domain.problem.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record ProblemWithBookmarkSummaryQueryPageDto(
        long totalCount,
        long totalPage,
        List<ProblemWithBookmarkSummaryQueryDto> problems
) {
    public static ProblemWithBookmarkSummaryQueryPageDto from(Page<ProblemWithBookmarkSummaryQueryDto> dtos) {
        return new ProblemWithBookmarkSummaryQueryPageDto(
                dtos.getTotalElements(),
                dtos.getTotalPages(),
                dtos.getContent()
        );
    }
}
