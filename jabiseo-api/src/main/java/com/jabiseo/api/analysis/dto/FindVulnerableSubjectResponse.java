package com.jabiseo.api.analysis.dto;

import com.jabiseo.domain.analysis.dto.VulnerableSubjectDto;

public record FindVulnerableSubjectResponse(
    Long subjectId,
    String subjectName,
    int vulnerableRate
) {
    public static FindVulnerableSubjectResponse from(VulnerableSubjectDto vulnerableSubjectDto) {
        return new FindVulnerableSubjectResponse(
            vulnerableSubjectDto.subjectId(),
            vulnerableSubjectDto.subjectName(),
            vulnerableSubjectDto.vulnerableRate()
        );
    }
}
