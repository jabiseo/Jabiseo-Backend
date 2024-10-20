package com.jabiseo.api.plan.dto.calender;

import java.util.List;

public record PlanCalenderSearchResponse(
        int year,
        int month,
        List<PlanProgressDateResponse> dailyProgress,
        List<PlanProgressDateResponse> weeklyProgress
) {
}
