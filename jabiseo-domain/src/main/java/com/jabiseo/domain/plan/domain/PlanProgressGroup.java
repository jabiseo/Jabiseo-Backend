package com.jabiseo.domain.plan.domain;

import com.jabiseo.domain.learning.domain.LearningMode;
import com.jabiseo.domain.plan.dto.LearningResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlanProgressGroup {

    private final List<PlanProgress> progresses;
    private final WeeklyDefineStrategy weeklyDefineStrategy;


    public PlanProgressGroup(List<PlanProgress> progresses, WeeklyDefineStrategy weeklyDefineStrategy) {
        this.progresses = List.copyOf(progresses);
        this.weeklyDefineStrategy = weeklyDefineStrategy;
    }

    public PlanProgressGroup findNew(List<PlanItem> requestItems) {
        List<PlanItem> result = requestItems.stream()
                .filter((requestItem) -> this.progresses.stream().noneMatch((progress -> progress.equalsItems(requestItem))))
                .toList();

        return new PlanProgressGroup(result.stream().map(this::toProgress).toList(), weeklyDefineStrategy);
    }

    public PlanProgressGroup findRemoved(List<PlanItem> requestItems) {
        List<PlanProgress> found = progresses.stream()
                .filter((progress -> requestItems.stream().noneMatch(progress::equalsItems)))
                .toList();
        return new PlanProgressGroup(found, weeklyDefineStrategy);
    }

    public PlanProgressGroup update(List<PlanItem> requestItems) {
        List<PlanProgress> result = new ArrayList<>();

        progresses.forEach(progress -> {
            PlanItem matchedItem = findMatchedItem(progress, requestItems);
            if (matchedItem != null) {
                progress.updateTargetValue(matchedItem.getTargetValue());
                result.add(progress);
            }
        });
        return new PlanProgressGroup(result, weeklyDefineStrategy);
    }

    public List<PlanProgress> getProgresses() {
        return progresses;
    }

    private PlanItem findMatchedItem(PlanProgress progress, List<PlanItem> items) {
        return items.stream()
                .filter(progress::equalsItems)
                .findFirst()
                .orElse(null);
    }

    public PlanProgressGroup calculate(List<LearningResult> learningResults) {
        Map<ActivityType, PlanProgress> map = this.progresses.stream()
                .collect(Collectors.toMap(
                        PlanProgress::getActivityType,
                        Function.identity()
                ));

        // O(n)으로 처리
        learningResults.forEach(learning -> {
            updateIfMatchesMode(map, ActivityType.EXAM, learning, LearningMode.EXAM, 1L);
            updateIfMatchesMode(map, ActivityType.STUDY, learning, LearningMode.STUDY, 1L);
            updatePlanProgressIfPresent(map, ActivityType.PROBLEM, learning.getSolvingCount());
            updatePlanProgressIfPresent(map, ActivityType.TIME, learning.getLearningTime());
        });

        List<PlanProgress> list = new ArrayList<>(map.values());
        return new PlanProgressGroup(list, weeklyDefineStrategy);
    }

    private void updateIfMatchesMode(Map<ActivityType, PlanProgress> map, ActivityType activityType,
                                     LearningResult learning, LearningMode targetMode, long value) {
        if (map.containsKey(activityType) && learning.getMode() == targetMode) {
            map.get(activityType).addCompletedValue(value);
        }
    }

    private void updatePlanProgressIfPresent(Map<ActivityType, PlanProgress> map, ActivityType type, long value) {
        if (map.containsKey(type)) {
            map.get(type).addCompletedValue(value);
        }
    }

    public PlanProgress toProgress(PlanItem item) {
        LocalDate date = LocalDate.now();

        if (item.getGoalType().equals(GoalType.WEEKLY)) {
            WeekPeriod currentWeekPeriod = weeklyDefineStrategy.getWeekPeriod(LocalDate.now());
            date = currentWeekPeriod.getEnd();
        }

        return item.toPlanProgress(date);
    }

    public WeekPeriod getWeekPeriod(LocalDate date){
        return this.weeklyDefineStrategy.getWeekPeriod(date);
    }


}
