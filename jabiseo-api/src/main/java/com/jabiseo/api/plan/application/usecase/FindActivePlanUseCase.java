package com.jabiseo.api.plan.application.usecase;

import com.jabiseo.api.plan.dto.ActivePlanResponse;
import com.jabiseo.api.plan.dto.PlanItemResponse;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.domain.plan.domain.GoalType;
import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.domain.PlanItem;
import com.jabiseo.domain.plan.domain.PlanRepository;
import com.jabiseo.domain.plan.exception.PlanBusinessException;
import com.jabiseo.domain.plan.exception.PlanErrorCode;
import com.jabiseo.api.problem.dto.CertificateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class FindActivePlanUseCase {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    public ActivePlanResponse execute(Long memberId) {
        Member member = memberRepository.getReferenceById(memberId);
        member.validateCurrentCertificate();

        Plan plan = planRepository.findFirstByCertificateAndMember(member.getCurrentCertificate(), member)
                .orElseThrow(() -> new PlanBusinessException(PlanErrorCode.NOT_FOUND_PLAN));

        List<PlanItem> planItems = plan.getPlanItems();

        return ActivePlanResponse.builder()
                .planId(String.valueOf(plan.getId()))
                .certificate(CertificateResponse.from(plan.getCertificate()))
                .endAt(plan.getEndAt())
                .createdAt(plan.getCreatedAt().toLocalDate())
                .weeklyPlanItems(planItems.stream().filter((p -> p.getGoalType().equals(GoalType.WEEKLY))).map(PlanItemResponse::from).toList())
                .dailyPlanItems(planItems.stream().filter((p -> p.getGoalType().equals(GoalType.DAILY))).map(PlanItemResponse::from).toList())
                .build();
    }

}
