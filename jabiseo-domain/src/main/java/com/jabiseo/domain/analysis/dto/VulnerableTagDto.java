package com.jabiseo.domain.analysis.dto;

public record VulnerableTagDto(
        Long tagId,
        String tagName,
        int vulnerableRate
) {
    public static VulnerableTagDto of(Long tagId, String tagName, int vulnerableRate) {
        return new VulnerableTagDto(tagId, tagName, vulnerableRate);
    }
}
