package com.jabiseo.domain.plan.service;


import com.jabiseo.domain.plan.domain.*;
import com.jabiseo.domain.plan.dto.LearningResult;
import com.jabiseo.domain.plan.repository.PlanProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanProgressService {

    private final WeeklyDefineStrategy weeklyDefineStrategy;
    private final PlanProgressCreateService planProgressCreateService;
    private final PlanProgressGroupFactory planProgressGroupFactory;
    private final PlanProgressRepository planProgressRepository;

    public List<PlanProgress> findByYearMonth(Plan plan, int year, int month) {
        List<WeekPeriod> periodPerWeek = weeklyDefineStrategy.getPeriodPerWeek(year, month);
        LocalDate startQueryDate = periodPerWeek.get(0).getStart();
        LocalDate endQueryDate = periodPerWeek.get(periodPerWeek.size() - 1).getEnd();

        return planProgressRepository.findAllByPlanAndProgressDateBetweenOrderByProgressDate(plan, startQueryDate, endQueryDate);
    }

    @Transactional
    public void updateProgress(Plan plan, LearningResult learningResult) {
        PlanProgressGroup progressGroup = planProgressGroupFactory.createWithInitialData(plan);

        List<PlanProgress> progresses = progressGroup.calculate(List.of(learningResult))
                                                    .getProgresses();
        planProgressRepository.saveAll(progresses);
    }


    @Transactional
    public void update(Plan plan, List<PlanItem> requestItems) {
        PlanProgressGroup progressGroup = planProgressGroupFactory.createWithInitialData(plan);

        // modify
        PlanProgressGroup updated = progressGroup.update(requestItems);
        planProgressRepository.saveAll(updated.getProgresses());


        // remove
        PlanProgressGroup removed = progressGroup.findRemoved(requestItems);
        planProgressRepository.deleteAll(removed.getProgresses());


        // create other proxy transaction call
        PlanProgressGroup newProgresses = progressGroup.findNew(requestItems);
        planProgressCreateService.create(newProgresses, plan);
    }


}
