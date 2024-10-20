package com.jabiseo.domain.analysis.dto;

public record VulnerableSubjectDto(
        Long subjectId,
        String subjectName,
        int vulnerableRate
) {
    public static VulnerableSubjectDto of(Long subjectId, String subjectName, int vulnerableRate) {
        return new VulnerableSubjectDto(subjectId, subjectName, vulnerableRate);
    }
}
