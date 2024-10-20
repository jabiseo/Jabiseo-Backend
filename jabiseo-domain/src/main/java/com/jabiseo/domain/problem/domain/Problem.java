package com.jabiseo.domain.problem.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.domain.Exam;
import com.jabiseo.domain.certificate.domain.Subject;
import com.jabiseo.domain.certificate.exception.CertificateBusinessException;
import com.jabiseo.domain.certificate.exception.CertificateErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Problem {

    @Id
    @Column(name = "problem_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String choice1;

    private String choice2;

    private String choice3;

    private String choice4;

    private int answerNumber;

    private String solution;

    private int sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Certificate certificate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_info_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ProblemInfo problemInfo;

    public List<String> getChoices() {
        return List.of(choice1, choice2, choice3, choice4);
    }

    private Problem(String description, String choice1, String choice2, String choice3, String choice4, int answerNumber,
                    String solution, int sequence, Certificate certificate, Exam exam, Subject subject, ProblemInfo problemInfo) {
        this.description = description;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.answerNumber = answerNumber;
        this.solution = solution;
        this.sequence = sequence;
        this.certificate = certificate;
        this.exam = exam;
        this.subject = subject;
        this.problemInfo = problemInfo;
    }

    public static Problem of(String description, String choice1, String choice2, String choice3, String choice4, int answerNumber,
                             String solution, int sequence, Certificate certificate, Exam exam, Subject subject, ProblemInfo problemInfo) {
        return new Problem(description, choice1, choice2, choice3, choice4,
                answerNumber, solution, sequence, certificate, exam, subject, problemInfo);
    }

    public void validateProblemInCertificate(Certificate certificate) {
        if (!this.getCertificate().equals(certificate)) {
            throw new CertificateBusinessException(CertificateErrorCode.PROBLEM_NOT_FOUND_IN_CERTIFICATE);
        }
    }

    public boolean checkAnswer(int choice) {
        return this.answerNumber == choice;
    }
}
