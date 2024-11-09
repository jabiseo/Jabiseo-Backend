package com.jabiseo.api.certificate.application.usecase;

import com.jabiseo.api.certificate.dto.FindCertificateDetailResponse;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.service.CertificateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static fixture.CertificateFixture.createCertificate;
import static fixture.ExamFixture.createExam;
import static fixture.SubjectFixture.createSubject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("자격증 정보 조회 테스트")
@ExtendWith(MockitoExtension.class)
class FindCertificateDetailUseCaseTest {

    @InjectMocks
    FindCertificateDetailUseCase sut;

    @Mock
    CertificateService certificateService;

    @Test
    @DisplayName("자격증 정보 조회를 성공한다.")
    void givenCertificateId_whenFindingCertificate_thenFindCertificate() {
        //given
        Long certificateId = 1L;
        Long examId = 2L;
        Long subjectId = 3L;
        Certificate certificate = createCertificate(certificateId);
        createExam(examId, certificate);
        createSubject(subjectId, certificate);
        given(certificateService.getByIdWithExamsAndSubjects(certificateId)).willReturn(certificate);

        //when
        FindCertificateDetailResponse response = sut.execute(certificateId);

        //then
        assertThat(response.certificateId()).isEqualTo(certificateId);
        assertThat(response.exams().get(0).examId()).isEqualTo(examId);
        assertThat(response.subjects().get(0).subjectId()).isEqualTo(subjectId);
    }

}
