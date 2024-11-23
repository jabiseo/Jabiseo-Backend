package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.dto.request.UpdateNicknameRequest;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static fixture.MemberFixture.createMember;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("닉네임 수정 유스케이스 테스트")
@ExtendWith(MockitoExtension.class)
class UpdateNicknameUseCaseTest {

    @InjectMocks
    UpdateNicknameUseCase updateNicknameUseCase;

    @Mock
    MemberService memberService;

    UpdateNicknameRequest request;

    @BeforeEach
    void setUp() {
        request = new UpdateNicknameRequest("newNickname");
    }

    @Test
    @DisplayName("닉네임 수정 성공")
    void updateNicknameUseCaseSuccess() {
        //given
        Member member = createMember(1L);
        given(memberService.updateNickname(member.getId(), request.nickname())).willReturn(member);

        //when
        updateNicknameUseCase.execute(member.getId(), request);

        //then
        verify(memberService).updateNickname(member.getId(), request.nickname());
    }
}
