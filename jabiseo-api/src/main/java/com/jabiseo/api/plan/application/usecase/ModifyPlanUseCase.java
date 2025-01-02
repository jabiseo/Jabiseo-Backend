package com.jabiseo.api.plan.application.usecase;

import com.jabiseo.api.plan.dto.request.ModifyPlanRequest;
import com.jabiseo.domain.plan.domain.*;
import com.jabiseo.domain.plan.service.PlanProgressService;
import com.jabiseo.domain.plan.service.PlanService;
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

    public void execute(Long planId, Long memberId, ModifyPlanRequest request) {
        Plan plan = planService.getPlanWithItems(planId);
        plan.checkOwner(memberId);


        List<PlanItem> requestPlanItems = request.toPlanItems(plan);
        planProgressService.update(plan, requestPlanItems);
        plan.modify(requestPlanItems, request.endAt());

        planService.savePlan(plan);
    }

}
