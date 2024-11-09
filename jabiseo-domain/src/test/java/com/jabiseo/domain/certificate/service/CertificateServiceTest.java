package com.jabiseo.domain.certificate.service;

import com.jabiseo.domain.certificate.domain.CertificateRepository;
import com.jabiseo.domain.certificate.exception.CertificateBusinessException;
import com.jabiseo.domain.certificate.exception.CertificateErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("CertificateService 테스트")
class CertificateServiceTest {

    @InjectMocks
    CertificateService sut;

    @Mock
    CertificateRepository certificateRepository;

    @Test
    @DisplayName("[getById() 테스트] 자격증이 존재하지 않을 경우 예외처리한다.")
    void givenNoCertificate_whenGetById_thenThrowException() {
        // given
        Long certificateId = 1L;
        given(certificateRepository.findById(certificateId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.getById(certificateId))
                .isInstanceOf(CertificateBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", CertificateErrorCode.CERTIFICATE_NOT_FOUND);
    }

    @Test
    @DisplayName("[getByIdWithExamsAndSubjects() 테스트] 자격증이 존재하지 않을 경우 예외처리한다.")
    void givenNoCertificate_whenGetByIdWithExamsAndSubjects_thenThrowException() {
        // given
        Long certificateId = 1L;
        given(certificateRepository.findByIdWithExams(certificateId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.getByIdWithExamsAndSubjects(certificateId))
                .isInstanceOf(CertificateBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", CertificateErrorCode.CERTIFICATE_NOT_FOUND);
    }

}
