package com.jabiseo.api.problem.dto.response;

import java.util.List;

public record FindProblemsResponse(
        CertificateResponse certificateInfo,
        List<ProblemsDetailResponse> problems
) {
    public static FindProblemsResponse of(CertificateResponse certificateInfo, List<ProblemsDetailResponse> problems) {
        return new FindProblemsResponse(certificateInfo, problems);
    }
}
