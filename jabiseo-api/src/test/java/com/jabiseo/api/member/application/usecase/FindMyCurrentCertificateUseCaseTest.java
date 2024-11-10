package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.dto.FindMyCurrentCertificateResponse;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static fixture.CertificateFixture.createCertificate;
import static fixture.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("회원의 현재 자격증 조회 테스트")
@ExtendWith(MockitoExtension.class)
class FindMyCurrentCertificateUseCaseTest {

    @InjectMocks
    FindMyCurrentCertificateUseCase sut;

    @Mock
    MemberService memberService;

    @Test
    @DisplayName("현재 자격증이 없는 회원의 자격증 조회를 하면 certificateId가 null이다.")
    void givenMemberId_whenFindingCurrentCertificate_thenFindCertificateStatus() {
        //given
        Long memberId = 1L;
        Member member = createMember(memberId);
        given(memberService.getByIdWithCertificate(memberId)).willReturn(member);

        //when
        FindMyCurrentCertificateResponse response = sut.execute(memberId);

        //then
        assertThat(response.memberId()).isEqualTo(memberId.toString());
        assertThat(response.certificateId()).isNull();
    }

    @Test
    @DisplayName("현재 자격증이 있는 회원의 자격증 조회를 하면 certificateId가 있다.")
    void givenMemberId_whenFindingCurrentCertificate_thenFindCertificate() {
        //given
        Long memberId = 1L;
        Long certificateId = 2L;
        Member member = createMember(memberId);
        Certificate certificate = createCertificate(certificateId);
        member.updateCurrentCertificate(certificate);
        given(memberService.getByIdWithCertificate(memberId)).willReturn(member);

        //when
        FindMyCurrentCertificateResponse response = sut.execute(memberId);

        //then
        assertThat(response.memberId()).isEqualTo(memberId.toString());
        assertThat(response.certificateId()).isEqualTo(certificateId);
    }

}
