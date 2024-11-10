package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.dto.UpdateMyCurrentCertificateRequest;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.service.CertificateService;
import com.jabiseo.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static fixture.CertificateFixture.createCertificate;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("현재 자격증 변경 테스트")
@ExtendWith(MockitoExtension.class)
class UpdateMyCurrentCertificateUseCaseTest {

    @InjectMocks
    UpdateMyCurrentCertificateUseCase sut;

    @Mock
    MemberService memberService;

    @Mock
    CertificateService certificateService;

    @Test
    @DisplayName("현재 자격증 변경을 성공한다.")
    void givenMemberIdAndCertificateId_whenUpdatingCurrentCertificate_thenUpdateCurrentCertificate() {
        //given
        Long memberId = 1L;
        Long certificateId = 2L;
        Certificate certificate = createCertificate(certificateId);
        given(certificateService.getById(certificateId)).willReturn(certificate);

        UpdateMyCurrentCertificateRequest request = new UpdateMyCurrentCertificateRequest(certificateId);

        //when
        sut.execute(memberId, request);

        //then
        verify(memberService).updateCurrentCertificate(memberId, certificate);
    }

}
