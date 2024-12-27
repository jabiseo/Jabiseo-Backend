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


        List<PlanItem> requestDailyPlanItems = request.getDailyPlanItems(plan);
        List<PlanItem> requestWeeklyPlanItems = request.getWeeklyPlanItems(plan);

        plan.modify(requestDailyPlanItems, requestWeeklyPlanItems, request.endAt());

        List<PlanItem> newItems = plan.getNewItems(requestDailyPlanItems, requestWeeklyPlanItems);
        planProgressService.createCurrentPlanProgress(member, newItems);

        List<PlanItem> existItems = plan.getExistItems(requestDailyPlanItems, requestWeeklyPlanItems);
        planProgressService.modifyCurrentPlanProgress(plan, filterGoalType(existItems, GoalType.DAILY), filterGoalType(existItems, GoalType.WEEKLY));

        List<PlanItem> deletedItems = plan.getDeletedItems(requestDailyPlanItems, requestWeeklyPlanItems);
        planProgressService.removeCurrentPlanProgress(plan, filterGoalType(deletedItems, GoalType.DAILY), filterGoalType(deletedItems, GoalType.WEEKLY));


        planService.savePlan(plan);
    }

    private List<PlanItem> filterGoalType(List<PlanItem> planItems, GoalType goalType) {
        return planItems.stream()
                .filter((planItem) -> planItem.getGoalType().equals(goalType))
                .toList();
    }
}
