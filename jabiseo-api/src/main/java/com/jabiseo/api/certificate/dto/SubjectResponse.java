package com.jabiseo.api.certificate.dto;

import com.jabiseo.domain.certificate.domain.Subject;

public record SubjectResponse(
        Long subjectId,
        int sequence,
        String name
) {
    public static SubjectResponse from(Subject subject) {
        return new SubjectResponse(subject.getId(), subject.getSequence(), subject.getName());
    }

    public static SubjectResponse of(Long subjectId, int sequence, String name) {
        return new SubjectResponse(subjectId, sequence, name);
    }
}
