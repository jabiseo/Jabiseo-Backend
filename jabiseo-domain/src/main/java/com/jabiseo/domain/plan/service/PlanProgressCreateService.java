package com.jabiseo.domain.plan.service;

import com.jabiseo.domain.learning.repository.LearningRepository;
import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.domain.PlanProgressGroup;
import com.jabiseo.domain.plan.domain.WeekPeriod;
import com.jabiseo.domain.plan.dto.LearningResult;
import com.jabiseo.domain.plan.repository.PlanProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class PlanProgressCreateService {

    private final LearningRepository learningRepository;
    private final PlanProgressRepository planProgressRepository;

    public void create(PlanProgressGroup newGroup, Plan plan) {
        WeekPeriod currentWeekPeriod = newGroup.getWeekPeriod(LocalDate.now());

        List<LearningResult> learnings = learningRepository.findLearningWithSolvingCount(plan.getMember(), plan.getCertificate(), currentWeekPeriod.getStart(), currentWeekPeriod.getEnd())
                .stream()
                .map(LearningResult::from).toList();

        PlanProgressGroup calculated = newGroup.calculate(learnings);
        planProgressRepository.saveAll(calculated.getProgresses());
    }
}
