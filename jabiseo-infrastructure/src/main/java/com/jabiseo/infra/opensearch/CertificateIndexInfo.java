package com.jabiseo.infra.opensearch;

import com.jabiseo.domain.analysis.exception.AnalysisBusinessException;
import com.jabiseo.domain.common.exception.CommonErrorCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
enum CertificateIndexInfo {

    JEONGBOCHEORIGISA(1L, "jeongbocheorigisa", "jeongbocheorigisa_subject", "jeongbocheorigisa_tag", "problem_vector", "subject_vector", "tag_vector"),
    ;

    private final Long certificateId;
    private final String problemIndexName;
    private final String subjectIndexName;
    private final String tagIndexName;
    private final String problemVectorName;
    private final String subjectVectorName;
    private final String tagVectorName;

    CertificateIndexInfo(Long certificateId, String problemIndexName, String subjectIndexName, String tagIndexName, String problemVectorName, String subjectVectorName, String tagVectorName) {
        this.certificateId = certificateId;
        this.problemIndexName = problemIndexName;
        this.subjectIndexName = subjectIndexName;
        this.tagIndexName = tagIndexName;
        this.problemVectorName = problemVectorName;
        this.subjectVectorName = subjectVectorName;
        this.tagVectorName = tagVectorName;
    }

    public static CertificateIndexInfo findByCertificateId(Long certificateId) {
        return Arrays.stream(CertificateIndexInfo.values())
                .filter(certificate -> Objects.equals(certificate.certificateId, certificateId))
                .findFirst()
                .orElseThrow(() -> new AnalysisBusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR));
    }
}
