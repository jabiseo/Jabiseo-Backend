package com.jabiseo.api.certificate.application.usecase;

import com.jabiseo.api.certificate.application.usecase.FindCertificateListUseCase;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.domain.CertificateRepository;
import com.jabiseo.api.certificate.dto.FindCertificateListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static fixture.CertificateFixture.createCertificate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("자격증 목록 조회 테스트")
@ExtendWith(MockitoExtension.class)
class FindCertificateListUseCaseTest {

    @InjectMocks
    FindCertificateListUseCase sut;

    @Mock
    CertificateRepository certificateRepository;

    @Test
    @DisplayName("자격증 목록 조회를 성공한다.")
    void givenCertificates_whenFindingCertificates_thenFindCertificates() {
        //given
        Long certificateId1 = 1L;
        Long certificateId2 = 2L;
        Certificate certificate1 = createCertificate(certificateId1);
        Certificate certificate2 = createCertificate(certificateId2);
        given(certificateRepository.findAll()).willReturn(List.of(certificate1, certificate2));

        //when
        List<FindCertificateListResponse> response = sut.execute();

        //then
        assertThat(response.get(0).certificateId()).isEqualTo(certificateId1);
        assertThat(response.get(1).certificateId()).isEqualTo(certificateId2);
    }

}
