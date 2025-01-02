package com.jabiseo.domain.plan.service;

import com.jabiseo.domain.plan.domain.*;
import com.jabiseo.domain.plan.repository.PlanProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlanProgressGroupFactory {

    private final PlanProgressRepository planProgressRepository;
    private final WeeklyDefineStrategy weeklyDefineStrategy;


    public PlanProgressGroup createWithInitialData(Plan plan) {
        List<PlanProgress> progressList = new ArrayList<>();
        PlanItemGroup planItemGroup = plan.getPlanItemGroup();

        if (planItemGroup.hasDailyItems()) {
            LocalDate start = LocalDate.now();
            LocalDate end = LocalDate.now();
            progressList.addAll(planProgressRepository.findAllByPlanAndProgressDateBetweenAndGoalType(plan, start, end, GoalType.DAILY));
        }

        if (planItemGroup.hasWeeklyItems()) {
            WeekPeriod period = weeklyDefineStrategy.getWeekPeriod(LocalDate.now());
            LocalDate start = period.getStart();
            LocalDate end = period.getEnd();
            progressList.addAll(planProgressRepository.findAllByPlanAndProgressDateBetweenAndGoalType(plan, start, end, GoalType.WEEKLY));
        }

        return new PlanProgressGroup(progressList, weeklyDefineStrategy);
    }

    public PlanProgressGroup createEmptyGroup() {
        return new PlanProgressGroup(new ArrayList<>(), weeklyDefineStrategy);
    }


}
