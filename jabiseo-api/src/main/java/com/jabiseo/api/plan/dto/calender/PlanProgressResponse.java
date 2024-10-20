package com.jabiseo.api.plan.dto.calender;

import com.jabiseo.domain.plan.domain.ActivityType;

public record PlanProgressResponse(
        ActivityType activityType,
        Long completedValue,
        Integer targetValue
) {
}
