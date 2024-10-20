package com.jabiseo.domain.certificate.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.exception.CertificateBusinessException;
import com.jabiseo.domain.certificate.exception.CertificateErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static fixture.CertificateFixture.createCertificate;
import static fixture.ExamFixture.createExam;
import static fixture.SubjectFixture.createSubject;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("자격증 도메인 테스트")
class CertificateTest {

    @Test
    @DisplayName("시험 id와 과목 id 리스트가 모두 적절하면 예외를 반환하지 않는다.")
    void givenExamIdAndSubjectIdList_whenValidateInput_thenDoNotReturnError() {
        //given
        Long certificateId = 1L;
        Long examId = 2L;
        List<Long> subjectIdList = List.of(3L, 4L);

        Certificate certificate = createCertificate(certificateId);
        createExam(examId, certificate);
        subjectIdList.forEach(subjectId -> createSubject(subjectId, certificate));

        //when & then
        assertDoesNotThrow(() -> certificate.validateExamIdAndSubjectIds(examId, subjectIdList));
    }

    @Test
    @DisplayName("시험 id가 없고 과목 id 리스트가 적절하면 예외를 반환하지 않는다.")
    void givenSubjectIdList_whenValidateInput_thenDoNotReturnError() {
        //given
        Long certificateId = 1L;
        List<Long> subjectIdList = List.of(3L, 4L);

        Certificate certificate = createCertificate(certificateId);
        subjectIdList.forEach(subjectId -> createSubject(subjectId, certificate));

        //when & then
        assertDoesNotThrow(() -> certificate.validateExamIdAndSubjectIds(null, subjectIdList));
    }

    @Test
    @DisplayName("시험 id가 자격증과 관련이 없으면 예외를 반환한다.")
    void givenInvalidExamIdAndSubjectIdList_whenValidateInput_thenReturnError() {
        //given
        Long certificateId = 1L;
        Long examId = 2L;
        Long invalidExamId = 100L;
        List<Long> subjectIdList = List.of(3L, 4L);

        Certificate certificate = createCertificate(certificateId);
        createExam(examId, certificate);
        subjectIdList.forEach(subjectId -> createSubject(subjectId, certificate));

        //when & then
        assertThatThrownBy(() -> certificate.validateExamIdAndSubjectIds(invalidExamId, subjectIdList))
                .isInstanceOf(CertificateBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", CertificateErrorCode.EXAM_NOT_FOUND_IN_CERTIFICATE);
    }

    @Test
    @DisplayName("과목 id 리스트가 자격증과 관련이 없으면 예외를 반환한다.")
    void givenExamIdAndInvalidSubjectIdList_whenValidateInput_thenReturnError() {
        //given
        Long certificateId = 1L;
        Long examId = 2L;
        List<Long> subjectIdList = List.of(3L, 4L);
        List<Long> invalidSubjectIdList = List.of(100L);

        Certificate certificate = createCertificate(certificateId);
        createExam(examId, certificate);
        subjectIdList.forEach(subjectId -> createSubject(subjectId, certificate));

        //when & then
        assertThatThrownBy(() -> certificate.validateExamIdAndSubjectIds(examId, invalidSubjectIdList))
                .isInstanceOf(CertificateBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", CertificateErrorCode.SUBJECT_NOT_FOUND_IN_CERTIFICATE);
    }
}
