package com.jabiseo.domain.problem.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ProblemInfo {

    @Id
    @Column(name = "problem_info_id")
    private Long id;

    private Long certificateId;

    private String certificateName;

    private Long examId;

    private String examDescription;

    private int examYear;

    private int examYearRound;

    private Long subjectId;

    private String subjectName;

    private int subjectSequence;

    private ProblemInfo(Long certificateId, String certificateName, Long examId, String examDescription,
                       int examYear, int examYearRound, Long subjectId, String subjectName, int subjectSequence) {
        this.certificateId = certificateId;
        this.certificateName = certificateName;
        this.examId = examId;
        this.examDescription = examDescription;
        this.examYear = examYear;
        this.examYearRound = examYearRound;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectSequence = subjectSequence;
    }

    public static ProblemInfo of(Long certificateId, String certificateName, Long examId, String examDescription,
                                 int examYear, int examYearRound, Long subjectId, String subjectName, int subjectSequence) {
        return new ProblemInfo(certificateId, certificateName, examId, examDescription, examYear, examYearRound, subjectId, subjectName, subjectSequence);
    }

}
