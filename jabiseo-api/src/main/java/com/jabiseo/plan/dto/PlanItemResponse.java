package com.jabiseo.plan.dto;

import com.jabiseo.plan.domain.ActivityType;
import com.jabiseo.plan.domain.PlanItem;

public record PlanItemResponse(
        String planItemId,
        ActivityType activityType,
        int targetValue
) {

    public static PlanItemResponse from(PlanItem planItem) {
        return new PlanItemResponse(String.valueOf(planItem.getId()), planItem.getActivityType(), planItem.getTargetValue());
    }
}
