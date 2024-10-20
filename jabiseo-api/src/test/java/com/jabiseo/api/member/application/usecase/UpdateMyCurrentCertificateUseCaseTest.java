package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.application.usecase.UpdateMyCurrentCertificateUseCase;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.domain.CertificateRepository;
import com.jabiseo.domain.certificate.exception.CertificateErrorCode;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.api.member.dto.UpdateMyCurrentCertificateRequest;
import com.jabiseo.domain.member.exception.MemberBusinessException;
import com.jabiseo.domain.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static fixture.CertificateFixture.createCertificate;
import static fixture.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("현재 자격증 변경 테스트")
@ExtendWith(MockitoExtension.class)
class UpdateMyCurrentCertificateUseCaseTest {

    @InjectMocks
    UpdateMyCurrentCertificateUseCase sut;

    @Mock
    MemberRepository memberRepository;

    @Mock
    CertificateRepository certificateRepository;

    @Test
    @DisplayName("현재 자격증 변경을 성공한다.")
    void givenMemberIdAndCertificateId_whenUpdatingCurrentCertificate_thenUpdateCurrentCertificate() {
        //given
        Long memberId = 1L;
        Long certificateId = 2L;
        Member member = createMember(memberId);
        Certificate certificate = createCertificate(certificateId);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(certificateRepository.findById(certificateId)).willReturn(Optional.of(certificate));

        //when
        UpdateMyCurrentCertificateRequest request = new UpdateMyCurrentCertificateRequest(certificateId);
        sut.execute(memberId, request);

        //then
        assertThat(member.getCurrentCertificate()).isEqualTo(certificate);
    }

    @Test
    @DisplayName("존재하지 않는 회원의 현재 자격증 변경을 시도하면 예외처리한다.")
    void givenCertificateIdAndNonExistedMemberId_whenUpdatingCurrentCertificate_thenReturnError() {
        //given
        Long nonExistedMemberId = 1L;
        Long certificateId = 2L;
        given(memberRepository.findById(nonExistedMemberId)).willReturn(Optional.empty());

        //when & then
        UpdateMyCurrentCertificateRequest request = new UpdateMyCurrentCertificateRequest(certificateId);
        assertThatThrownBy(() -> sut.execute(nonExistedMemberId, request))
                .isInstanceOf(MemberBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.MEMBER_NOT_FOUND);

    }

    @Test
    @DisplayName("존재하지 않는 자격증으로 현재 자격증 변경을 시도하면 예외처리한다.")
    void givenMemberIdAndNonExistedCertificateId_whenUpdatingCurrentCertificate_thenReturnError() {
        //given
        Long memberId = 1L;
        Long nonExistedCertificateId = 2L;
        Member member = createMember(memberId);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(certificateRepository.findById(nonExistedCertificateId)).willReturn(Optional.empty());

        //when & then
        UpdateMyCurrentCertificateRequest request = new UpdateMyCurrentCertificateRequest(nonExistedCertificateId);
        assertThatThrownBy(() -> sut.execute(memberId, request))
                .isInstanceOf(MemberBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", CertificateErrorCode.CERTIFICATE_NOT_FOUND);
    }

}
