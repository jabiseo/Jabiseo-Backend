package com.jabiseo.member.usecase;

import com.jabiseo.certificate.domain.Certificate;
import com.jabiseo.member.domain.Member;
import com.jabiseo.member.domain.MemberRepository;
import com.jabiseo.member.dto.FindMyCertificateStateResponse;
import com.jabiseo.member.exception.MemberBusinessException;
import com.jabiseo.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.jabiseo.fixture.CertificateFixture.createCertificate;
import static com.jabiseo.fixture.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("자격증 상태 조회 테스트")
@ExtendWith(MockitoExtension.class)
class FindMyCertificateStateUseCaseTest {

    @InjectMocks
    FindMyCertificateStateUseCase sut;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("자격증 상태 조회")
    void givenMemberId_whenFindingCertificateState_thenFindCertificateStatus() throws Exception {
        //given
        String memberId = "1";
        String certificateId = "2";
        Certificate certificate = createCertificate(certificateId);
        Member member = createMember(memberId);
        member.updateCertificateState(certificate);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        //when
        FindMyCertificateStateResponse response = sut.execute(memberId);

        //then
        assertThat(response.memberId()).isEqualTo(memberId);
        assertThat(response.certificateId()).isEqualTo(certificateId);
    }

    @Test
    @DisplayName("존재하지 않는 회원의 자격증 상태 조회 ")
    void givenNonExistedMemberId_whenFindingCertificateState_thenReturnError() throws Exception {
        //given
        String nonExistedMemberId = "1";
        given(memberRepository.findById(nonExistedMemberId)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> sut.execute(nonExistedMemberId))
                .isInstanceOf(MemberBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.MEMBER_NOT_FOUND);
    }

}