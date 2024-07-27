package com.jabiseo.member.application.usecase;

import com.jabiseo.certificate.domain.Certificate;
import com.jabiseo.member.domain.Member;
import com.jabiseo.member.domain.MemberRepository;
import com.jabiseo.member.dto.FindMyCurrentCertificateResponse;
import com.jabiseo.member.exception.MemberBusinessException;
import com.jabiseo.member.exception.MemberErrorCode;
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

@DisplayName("회원의 현재 자격증 조회 테스트")
@ExtendWith(MockitoExtension.class)
class FindMyCurrentCertificateUseCaseTest {

    @InjectMocks
    FindMyCurrentCertificateUseCase sut;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("현재 자격증 조회를 성공하면 현재 자격증을 반환한다.")
    void givenMemberId_whenFindingCurrentCertificate_thenFindCertificateStatus() {
        //given
        String memberId = "1";
        Long certificateId = 2L;
        Certificate certificate = createCertificate(certificateId);
        Member member = createMember(memberId);
        member.updateCurrentCertificate(certificate);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        //when
        FindMyCurrentCertificateResponse response = sut.execute(memberId);

        //then
        assertThat(response.memberId()).isEqualTo(memberId);
        assertThat(response.certificateId()).isEqualTo(certificateId);
    }

    @Test
    @DisplayName("존재하지 않는 회원의 현재 자격증 조회를 시도하면 예외처리한다.")
    void givenNonExistedMemberId_whenFindingCurrentCertificate_thenReturnError() {
        //given
        String nonExistedMemberId = "1";
        given(memberRepository.findById(nonExistedMemberId)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> sut.execute(nonExistedMemberId))
                .isInstanceOf(MemberBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.MEMBER_NOT_FOUND);
    }

}
