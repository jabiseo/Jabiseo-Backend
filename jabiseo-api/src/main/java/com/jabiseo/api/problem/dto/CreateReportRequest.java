package com.jabiseo.api.problem.dto;

import com.jabiseo.domain.problem.domain.ReportType;

public record CreateReportRequest(
        String problemId,
        ReportType reportType,
        String content
) {
}
