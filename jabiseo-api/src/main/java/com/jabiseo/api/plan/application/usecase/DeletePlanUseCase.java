package com.jabiseo.api.plan.application.usecase;

import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.domain.PlanRepository;
import com.jabiseo.domain.plan.domain.PlanService;
import com.jabiseo.domain.plan.exception.PlanBusinessException;
import com.jabiseo.domain.plan.exception.PlanErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeletePlanUseCase {

    private final PlanService planService;
    private final PlanRepository planRepository;

    public void execute(Long planId, Long memberId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanBusinessException(PlanErrorCode.NOT_FOUND_PLAN));

        plan.checkOwner(memberId);

        planService.removePlan(plan);
    }
}
