package com.jabiseo.domain.member.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.exception.MemberBusinessException;
import com.jabiseo.domain.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static fixture.CertificateFixture.createCertificate;
import static fixture.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("회원 도메인 테스트")
class MemberTest {

    @Test
    @DisplayName("회원의 현재 자격증이 존재해서 현재 자격증 존재 검사에서 예외가 발생하지 않는다.")
    void givenMember_whenValidateCurrentCertificate_thenDoesNotThrowException() {
        //given
        Long memberId = 1L;
        Long certificateId = 2L;
        Member member = createMember(memberId);
        Certificate certificate = createCertificate(certificateId);
        member.updateCurrentCertificate(certificate);

        //when & then
        assertDoesNotThrow(member::validateCurrentCertificate);
    }

    @Test
    @DisplayName("회원의 현재 자격증이 존재하지 않아 현재 자격증 존재 검사에서 예외가 발생한다.")
    void givenMemberWithoutCurrentCertificate_whenValidateCurrentCertificate_thenThrowException() {
        //given
        Long memberId = 1L;
        Member member = createMember(memberId);

        //when & then
        assertThatThrownBy(member::validateCurrentCertificate)
                .isInstanceOf(MemberBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.CURRENT_CERTIFICATE_NOT_EXIST);
    }

    @Test
    @DisplayName("회원의 닉네임을 변경할 수 있다")
    void nicknameUpdateSuccess(){
        //given
        Long memberId = 1L;
        Member member = createMember(memberId);
        String newNickname = "newNickname";

        // when
        member.updateNickname(newNickname);

        //then
        assertThat(member.getNickname()).isEqualTo(newNickname);
    }
}
