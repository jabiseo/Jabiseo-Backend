package com.jabiseo.api.plan.application.usecase;

import com.jabiseo.api.plan.dto.request.CreatePlanRequest;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.domain.PlanItem;
import com.jabiseo.domain.plan.domain.PlanItemGroup;
import com.jabiseo.domain.plan.domain.PlanProgressGroup;
import com.jabiseo.domain.plan.service.PlanProgressCreateService;
import com.jabiseo.domain.plan.service.PlanProgressGroupFactory;
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
    private final PlanProgressCreateService planProgressCreateService;
    private final PlanProgressGroupFactory planProgressGroupFactory;

    public Long execute(Long memberId, CreatePlanRequest request) {
        Member member = memberService.getByIdWithCertificate(memberId);
        member.validateCurrentCertificate();
        planService.checkInProgressPlan(member);

        Plan plan = Plan.create(member, request.endAt());
        PlanItemGroup planItemGroup = new PlanItemGroup(request.toPlanItems(plan));
        plan.updatePlanItemGroup(planItemGroup);

        Plan savedPlan = planService.savePlan(plan);

        PlanProgressGroup group = planProgressGroupFactory.createEmptyGroup()
                                                        .findNew(planItemGroup.getPlanItems());
        planProgressCreateService.create(group, plan);
        return savedPlan.getId();
    }

}
