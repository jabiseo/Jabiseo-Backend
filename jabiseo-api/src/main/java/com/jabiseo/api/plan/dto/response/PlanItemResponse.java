package com.jabiseo.api.plan.dto.response;

import com.jabiseo.domain.plan.domain.ActivityType;
import com.jabiseo.domain.plan.domain.PlanItem;

public record PlanItemResponse(
        String planItemId,
        ActivityType activityType,
        int targetValue
) {

    public static PlanItemResponse from(PlanItem planItem) {
        return new PlanItemResponse(String.valueOf(planItem.getId()), planItem.getActivityType(), planItem.getTargetValue());
    }
}
