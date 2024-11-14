package com.jabiseo.api.auth.application.usecase;

import com.jabiseo.api.auth.application.JwtHandler;
import com.jabiseo.api.auth.dto.ReissueRequest;
import com.jabiseo.api.auth.dto.ReissueResponse;
import com.jabiseo.domain.auth.domain.Auth;
import com.jabiseo.domain.auth.domain.AuthService;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import fixture.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReissueUseCaseTest {

    @InjectMocks
    ReissueUseCase reissueUseCase;

    @Mock
    MemberService memberService;

    @Mock
    AuthService authService;

    @Mock
    JwtHandler jwtHandler;

    ReissueRequest request;
    String deviceId;

    @BeforeEach
    void setUp() {
        request = new ReissueRequest("refresh");
        deviceId = "123";
    }

    @Test
    @DisplayName("정상 요청의 경우 새로운 access Token을 발급한다.")
    void requestSuccessReturnNewAccessToken() {
        //given
        Long memberId = 1L;
        Member member = MemberFixture.createMember(memberId);
        String newAccessToken = "accessToken";
        given(memberService.getById(memberId)).willReturn(member);
        given(jwtHandler.createAccessToken(member)).willReturn(newAccessToken);

        //when
        ReissueResponse execute = reissueUseCase.execute(request, memberId, deviceId);

        //then
        assertThat(execute.accessToken()).isEqualTo(newAccessToken);
        verify(authService, times(1)).reissue(Auth.create(deviceId, memberId, request.refreshToken()));
    }
}
