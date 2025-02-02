package com.jabiseo.domain.plan.service;

import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.plan.repository.PlanRepository;
import com.jabiseo.domain.plan.exception.PlanBusinessException;
import fixture.MemberFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @InjectMocks
    private PlanService sut;

    @Mock
    private PlanRepository planRepository;

    @Test
    @DisplayName("이미 진행중인 플랜이 있는 경우 예외를 반환한다")
    void existInProgressPlanThrownException() {
        //given
        Member member = MemberFixture.createMember();

        given(planRepository.existsByCertificateAndMember(member.getCurrentCertificate(), member))
                .willReturn(true);
        //when then
        Assertions.assertThatThrownBy(()-> sut.checkInProgressPlan(member))
                .isInstanceOf(PlanBusinessException.class);


    }

}
