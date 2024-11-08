package com.jabiseo.api.plan.application.usecase;

import com.jabiseo.api.plan.dto.ActivePlanResponse;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.domain.PlanItem;
import com.jabiseo.domain.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FindActivePlanUseCase {

    private final MemberService memberService;
    private final PlanService planService;

    public ActivePlanResponse execute(Long memberId) {
        Member member = memberService.getByIdWithCertificate(memberId);
        member.validateCurrentCertificate();

        Plan plan = planService.findFirstByCertificateAndMemberWithPlanItems(member.getCurrentCertificate(), member);

        List<PlanItem> planItems = plan.getPlanItems();

        return ActivePlanResponse.of(plan, planItems);
    }
}
