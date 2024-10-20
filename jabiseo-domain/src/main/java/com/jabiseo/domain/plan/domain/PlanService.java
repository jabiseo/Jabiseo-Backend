package com.jabiseo.domain.plan.domain;

import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.plan.exception.PlanBusinessException;
import com.jabiseo.domain.plan.exception.PlanErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanItemRepository planItemRepository;
    private final PlanProgressRepository planProgressRepository;

    public Plan getPlanWithItems(Long planId) {
        return planRepository.findPlanWithItemsById(planId)
                .orElseThrow(() -> new PlanBusinessException(PlanErrorCode.NOT_FOUND_PLAN));
    }

    public Plan savePlanAndItems(Plan plan, List<PlanItem> planItems) {
        planItemRepository.saveAll(planItems);
        return planRepository.save(plan);
    }


    public void checkInProgressPlan(Member member) {
        if (planRepository.existsByCertificateAndMember(member.getCurrentCertificate(), member)) {
            throw new PlanBusinessException(PlanErrorCode.ALREADY_EXIST_PLAN);
        }
    }

    public void removePlan(Plan plan){
        planRepository.delete(plan); // cascade 설정으로 가지고 있는 planItem 들도 모두 삭제 된다
        planProgressRepository.deleteByPlanId(plan.getId()); // plan progress를 삭제한다.
    }

}
