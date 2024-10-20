package com.jabiseo.api.analysis.dto;

import com.jabiseo.domain.analysis.dto.VulnerableTagDto;

public record FindVulnerableTagResponse(
    Long tagId,
    String tagName,
    int vulnerableRate
) {
    public static FindVulnerableTagResponse from(VulnerableTagDto vulnerableTagDto) {
        return new FindVulnerableTagResponse(
            vulnerableTagDto.tagId(),
            vulnerableTagDto.tagName(),
            vulnerableTagDto.vulnerableRate()
        );
    }
}
