package com.jabiseo.api.plan.application.usecase;

import com.jabiseo.api.plan.dto.request.CreatePlanRequest;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.domain.PlanItem;
import com.jabiseo.domain.plan.service.PlanProgressService;
import com.jabiseo.domain.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CreatePlanUseCase {

    private final MemberService memberService;
    private final PlanService planService;
    private final PlanProgressService planProgressService;

    public Long execute(Long memberId, CreatePlanRequest request) {
        Member member = memberService.getByIdWithCertificate(memberId);
        member.validateCurrentCertificate();
        planService.checkInProgressPlan(member);

        Plan plan = Plan.create(member, request.endAt());
        List<PlanItem> planItems = request.toPlanItems(plan);

        Plan savedPlan = planService.savePlanAndItems(plan, planItems);
        planProgressService.createCurrentPlanProgress(member, planItems);
        return savedPlan.getId();
    }

}
