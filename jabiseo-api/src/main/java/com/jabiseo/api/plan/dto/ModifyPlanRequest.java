package com.jabiseo.api.plan.dto;

import com.jabiseo.domain.plan.domain.ActivityType;
import com.jabiseo.domain.plan.domain.GoalType;
import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.domain.PlanItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ModifyPlanRequest(
        @NotNull @Future
        LocalDate endAt,

        @NotNull @Valid
        List<PlanItemRequest> dailyPlan,

        @NotNull @Valid
        List<PlanItemRequest> weeklyPlan
) {

    public List<PlanItem> getDailyPlanItems(Plan plan) {
        return dailyPlan.stream().map((item) -> new PlanItem(plan, ActivityType.valueOf(item.activityType()), GoalType.DAILY, item.targetValue())).toList();
    }

    public List<PlanItem> getWeeklyPlanItems(Plan plan) {
        return weeklyPlan.stream().map((item) -> new PlanItem(plan, ActivityType.valueOf(item.activityType()), GoalType.WEEKLY, item.targetValue())).toList();
    }
}
