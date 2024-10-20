package com.jabiseo.api.plan.application.usecase;

import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.domain.plan.domain.*;
import com.jabiseo.api.plan.dto.ModifyPlanRequest;
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
    private final MemberRepository memberRepository;

    public void execute(Long planId, Long memberId, ModifyPlanRequest request) {
        Plan plan = planService.getPlanWithItems(planId);
        Member member = memberRepository.getReferenceById(memberId);
        plan.checkOwner(memberId);

        plan.modifyEndAt(request.endAt());


        List<PlanItem> requestDailyPlanItems = request.getDailyPlanItems(plan);
        List<PlanItem> requestWeeklyPlanItems = request.getWeeklyPlanItems(plan);

        // 새로운 아이템은 플랜에 추가
        List<PlanItem> newItems = plan.getNewItems(requestDailyPlanItems, requestWeeklyPlanItems);
        planProgressService.createCurrentPlanProgress(member, newItems);

        // 기존 아이템은 수정
        List<PlanItem> existItems = plan.getExistItems(requestDailyPlanItems, requestWeeklyPlanItems);
        planProgressService.modifyCurrentPlanProgress(plan, filterGoalType(existItems, GoalType.DAILY), filterGoalType(existItems, GoalType.WEEKLY));

        // 없어지는 아이템은 삭제
        List<PlanItem> deletedItems = plan.getDeletedItems(requestDailyPlanItems, requestWeeklyPlanItems);
        planProgressService.removeCurrentPlanProgress(plan, filterGoalType(deletedItems, GoalType.DAILY), filterGoalType(deletedItems, GoalType.WEEKLY));

        // Plan 객체의 정합성을 유지(DB 저장)
        plan.modifyPlanItems(existItems, newItems, deletedItems);
    }

    private List<PlanItem> filterGoalType(List<PlanItem> planItems, GoalType goalType) {
        return planItems.stream()
                .filter((planItem) -> planItem.getGoalType().equals(goalType))
                .toList();
    }
}
