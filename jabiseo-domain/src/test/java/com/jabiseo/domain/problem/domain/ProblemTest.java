package com.jabiseo.domain.problem.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.exception.CertificateBusinessException;
import com.jabiseo.domain.certificate.exception.CertificateErrorCode;
import com.jabiseo.domain.problem.domain.Problem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static fixture.CertificateFixture.createCertificate;
import static fixture.ProblemFixture.createProblem;
import static fixture.ProblemFixture.createProblemWithAnswer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("문제 도메인 테스트")
class ProblemTest {

    @Test
    @DisplayName("문제 검사 시 파라미터로 들어온 자격증이 문제의 자격증에 해당하면 예외가 발생하지 않는다.")
    void givenCertificateAndProblem_whenValidateProblem_thenDoesNotReturnError() {
        //given
        Long certificateId = 1L;
        Long problemId = 2L;
        Certificate certificate = createCertificate(certificateId);
        Problem problem = createProblem(problemId, certificate);

        //when & then
        assertDoesNotThrow(() -> problem.validateProblemInCertificate(certificate));
    }

    @Test
    @DisplayName("문제 검사 시 파라미터로 들어온 자격증이 문제의 자격증에 해당하지 않으면 예외가 발생한다.")
    void givenInvalidCertificateAndProblem_whenValidateProblem_thenReturnError() {
        //given
        Long certificateId = 1L;
        Long invalidCertificateId = 100L;
        Long problemId = 2L;
        Certificate certificate = createCertificate(certificateId);
        Certificate invalidCertificate = createCertificate(invalidCertificateId);
        Problem problem = createProblem(problemId, certificate);

        //when & then
        assertThatThrownBy(() -> problem.validateProblemInCertificate(invalidCertificate))
                .isInstanceOf(CertificateBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", CertificateErrorCode.PROBLEM_NOT_FOUND_IN_CERTIFICATE);
    }

    @ParameterizedTest
    @DisplayName("문제를 채점한다.")
    @CsvSource({"1, false", "2, false", "3, true", "4, false"})
    void givenProblemAndChoice_whenCheckProblem_thenCheckAnswer(int choice, boolean checkResult) {
        //given
        Long certificateId = 1L;
        Long problemId = 2L;

        Certificate certificate = createCertificate(certificateId);
        Problem problem = createProblemWithAnswer(problemId, 3, certificate);

        //when & then
        assertThat(problem.checkAnswer(choice)).isEqualTo(checkResult);
    }

}
