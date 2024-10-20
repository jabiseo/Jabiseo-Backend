package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.application.usecase.UpdateNicknameUseCase;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.api.member.dto.UpdateNicknameRequest;
import com.jabiseo.api.member.dto.UpdateNicknameResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static fixture.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("닉네임 수정 유스케이스 테스트")
@ExtendWith(MockitoExtension.class)
class UpdateNicknameUseCaseTest {

    @InjectMocks
    UpdateNicknameUseCase updateNicknameUseCase;

    @Mock
    MemberRepository memberRepository;

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
        given(memberRepository.getReferenceById(member.getId())).willReturn(member);

        //when
        UpdateNicknameResponse result = updateNicknameUseCase.execute(member.getId(), request);

        //then
        assertThat(result.nickname()).isEqualTo(request.nickname());
    }
}
