package com.jabiseo.api.plan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jabiseo.api.problem.dto.CertificateResponse;
import com.jabiseo.domain.plan.domain.GoalType;
import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.domain.PlanItem;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record ActivePlanResponse(
        String planId,
        CertificateResponse certificate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate endAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate createdAt,
        List<PlanItemResponse> dailyPlanItems,
        List<PlanItemResponse> weeklyPlanItems
) {
    public static ActivePlanResponse of(Plan plan, List<PlanItem> planItems) {
        return ActivePlanResponse.builder()
                .planId(String.valueOf(plan.getId()))
                .certificate(CertificateResponse.from(plan.getCertificate()))
                .endAt(plan.getEndAt())
                .createdAt(plan.getCreatedAt().toLocalDate())
                .weeklyPlanItems(planItems.stream().filter((p -> p.getGoalType().equals(GoalType.WEEKLY))).map(PlanItemResponse::from).toList())
                .dailyPlanItems(planItems.stream().filter((p -> p.getGoalType().equals(GoalType.DAILY))).map(PlanItemResponse::from).toList())
                .build();
    }
}
