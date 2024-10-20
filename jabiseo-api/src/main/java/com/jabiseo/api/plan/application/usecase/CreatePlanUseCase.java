package com.jabiseo.api.plan.application.usecase;

import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.domain.PlanItem;
import com.jabiseo.domain.plan.domain.PlanProgressService;
import com.jabiseo.domain.plan.domain.PlanService;
import com.jabiseo.api.plan.dto.CreatePlanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CreatePlanUseCase {

    private final MemberRepository memberRepository;
    private final PlanService planService;
    private final PlanProgressService planProgressService;

    public Long execute(Long memberId, CreatePlanRequest request) {
        Member member = memberRepository.getReferenceById(memberId);
        member.validateCurrentCertificate();
        planService.checkInProgressPlan(member);

        Plan plan = Plan.create(member, request.endAt());
        List<PlanItem> planItems = request.toPlanItems(plan);

        Plan savedPlan = planService.savePlanAndItems(plan, planItems);
        planProgressService.createCurrentPlanProgress(member, planItems);
        return savedPlan.getId();
    }

}
