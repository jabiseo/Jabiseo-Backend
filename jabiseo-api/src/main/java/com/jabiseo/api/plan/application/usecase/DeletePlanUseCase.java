package com.jabiseo.api.plan.application.usecase;

import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletePlanUseCase {

    private final PlanService planService;

    public void execute(Long planId, Long memberId) {
        Plan plan = planService.getById(planId);

        plan.checkOwner(memberId);

        planService.removePlan(plan);
    }
}
