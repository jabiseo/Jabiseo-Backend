package com.jabiseo.domain.plan.service;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.plan.domain.*;
import com.jabiseo.domain.plan.exception.PlanBusinessException;
import com.jabiseo.domain.plan.exception.PlanErrorCode;
import com.jabiseo.domain.plan.repository.PlanItemRepository;
import com.jabiseo.domain.plan.repository.PlanProgressRepository;
import com.jabiseo.domain.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanItemRepository planItemRepository;
    private final PlanProgressRepository planProgressRepository;

    public Plan getById(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new PlanBusinessException(PlanErrorCode.NOT_FOUND_PLAN));
    }

    public Plan getByIdWithMember(Long planId) {
        return planRepository.findByIdWithMember(planId)
                .orElseThrow(() -> new PlanBusinessException(PlanErrorCode.NOT_FOUND_PLAN));
    }

    public Plan getPlanWithItems(Long planId) {
        return planRepository.findPlanWithItemsById(planId)
                .orElseThrow(() -> new PlanBusinessException(PlanErrorCode.NOT_FOUND_PLAN));
    }

    @Transactional
    public Plan savePlan(Plan plan) {
        return planRepository.save(plan);
    }

    public void checkInProgressPlan(Member member) {
        if (planRepository.existsByCertificateAndMember(member.getCurrentCertificate(), member)) {
            throw new PlanBusinessException(PlanErrorCode.ALREADY_EXIST_PLAN);
        }
    }

    @Transactional
    public void removePlan(Plan plan){
        planRepository.delete(plan); // cascade 설정으로 가지고 있는 planItem 들도 모두 삭제 된다
        planProgressRepository.deleteByPlanId(plan.getId()); // plan progress를 삭제한다.
    }

    public Plan findFirstByCertificateAndMemberWithPlanItems(Certificate currentCertificate, Member member) {
        return planRepository.findFirstByCertificateAndMemberWithPlanItems(currentCertificate, member)
                .orElseThrow(() -> new PlanBusinessException(PlanErrorCode.NOT_FOUND_PLAN));
    }
}
