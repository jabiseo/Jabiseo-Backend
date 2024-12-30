package com.jabiseo.api.plan.application.usecase;

import com.jabiseo.api.plan.dto.request.ModifyPlanRequest;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.domain.plan.domain.*;
import com.jabiseo.domain.plan.service.PlanProgressService;
import com.jabiseo.domain.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ModifyPlanUseCase {

    private final PlanService planService;
    private final PlanProgressService planProgressService;
    private final MemberService memberService;

    public void execute(Long planId, Long memberId, ModifyPlanRequest request) {
        Plan plan = planService.getPlanWithItems(planId);
        Member member = memberService.getByIdWithCertificate(memberId);
        plan.checkOwner(memberId);


        List<PlanItem> requestPlanItems = request.toPlanItems(plan);

        List<PlanItem> newItems = plan.getNewItems(requestPlanItems);
        planProgressService.createCurrentPlanProgress(member, newItems);

        List<PlanItem> existItems = plan.getExistItems(requestPlanItems);
        planProgressService.modifyCurrentPlanProgress(plan, filterGoalType(existItems, GoalType.DAILY), filterGoalType(existItems, GoalType.WEEKLY));

        List<PlanItem> deletedItems = plan.getDeletedItems(requestPlanItems);
        planProgressService.removeCurrentPlanProgress(plan, filterGoalType(deletedItems, GoalType.DAILY), filterGoalType(deletedItems, GoalType.WEEKLY));

        plan.modify(requestPlanItems, request.endAt());

        planService.savePlan(plan);
    }

    private List<PlanItem> filterGoalType(List<PlanItem> planItems, GoalType goalType) {
        return planItems.stream()
                .filter((planItem) -> planItem.getGoalType().equals(goalType))
                .toList();
    }
}
