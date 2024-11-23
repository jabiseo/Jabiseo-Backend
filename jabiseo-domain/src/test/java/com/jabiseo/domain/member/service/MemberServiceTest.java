package com.jabiseo.domain.member.service;

import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.repository.MemberRepository;
import com.jabiseo.domain.member.domain.OauthMemberInfo;
import com.jabiseo.domain.member.domain.OauthServer;
import com.jabiseo.domain.member.exception.MemberBusinessException;
import com.jabiseo.domain.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static fixture.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("MemberService 테스트")
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService sut;

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberFactory memberFactory;

    @Test
    @DisplayName("[getById 테스트] 존재하지 않는 memberId로 조회 시 예외가 발생한다.")
    void getByIdWithNotExistMemberId() {
        //given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> sut.getById(memberId))
                .isInstanceOf(MemberBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("[getByIdWithCertificate 테스트] 존재하지 않는 memberId로 조회 시 예외가 발생한다.")
    void getByIdWithCertificateWithNotExistMemberId() {
        //given
        Long memberId = 1L;
        given(memberRepository.findByIdWithCertificate(memberId)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> sut.getByIdWithCertificate(memberId))
                .isInstanceOf(MemberBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("처음 요청 오는 OAuth 회원일시 맴버 객체를 생성하고 저장한다.")
    void firstOauthUserIsSignUpAndSave() {
        //given
        OauthMemberInfo memberInfo = new OauthMemberInfo("id", OauthServer.KAKAO, "email@emil.com");
        Member member = createMember(1L);
        given(memberRepository.findByOauthIdAndOauthServer(memberInfo.getOauthId(), memberInfo.getOauthServer())).willReturn(Optional.empty());
        given(memberFactory.createNew(memberInfo)).willReturn(member);
        given(memberRepository.save(any())).willReturn(member);

        //when
        sut.getByOauthIdAndOauthServerOrCreateMember(memberInfo);

        //then
        verify(memberFactory, times(1)).createNew(memberInfo);
        verify(memberRepository, times(1)).save(member);
    }

}
