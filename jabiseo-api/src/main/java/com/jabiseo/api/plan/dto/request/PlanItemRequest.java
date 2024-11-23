package com.jabiseo.api.plan.dto.request;

import com.jabiseo.api.common.validator.EnumValid;
import com.jabiseo.domain.plan.domain.ActivityType;

public record PlanItemRequest(
        int targetValue,
        @EnumValid(enumClass = ActivityType.class, message = "허용된 plan activity 가 아닙니다")
        String activityType
) {
}
