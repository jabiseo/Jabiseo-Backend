package com.jabiseo.domain.plan.domain;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Embeddable
public class PlanItemGroup {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<PlanItem> planItems = new ArrayList<>();

    public PlanItemGroup(List<PlanItem> planItems) {
        this.planItems = planItems;
    }

    public PlanItemGroup() {
    }

    public void modifyPlanItems(List<PlanItem> daily, List<PlanItem> weekly) {
        List<PlanItem> newItems = getNewItems(daily, weekly);
        List<PlanItem> existItems = getExistItems(daily, weekly);
        List<PlanItem> deletedItems = getDeletedItems(daily, weekly);

        // 삭제될 아이템 삭제
        planItems.removeAll(deletedItems);

        // 이미 존재하는 items의 target value 수정 N*M
        planItems.forEach((planItem -> planItem.updateTargetValue(findTargetValue(existItems, planItem))));

        // 새 아이템 추가
        planItems.addAll(newItems);
    }

    public List<PlanItem> getPlanItems() {
        return planItems;
    }

    private int findTargetValue(List<PlanItem> source, PlanItem target) {
        PlanItem find = source.stream()
                .filter((item) -> item.getGoalType().equals(target.getGoalType()) && item.getActivityType().equals(target.getActivityType()))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Target value not found"));
        return find.getTargetValue();
    }

    public List<PlanItem> getNewItems(List<PlanItem> daily, List<PlanItem> weekly) {
        List<PlanItem> dailyNewItems = new ArrayList<>(getNewByGoalType(daily, GoalType.DAILY));
        dailyNewItems.addAll(getNewByGoalType(weekly, GoalType.WEEKLY));
        return dailyNewItems;
    }

    public List<PlanItem> getExistItems(List<PlanItem> daily, List<PlanItem> weekly) {
        List<PlanItem> dailyExistItems = new ArrayList<>(getExistByGoalType(daily, GoalType.DAILY));
        dailyExistItems.addAll(getExistByGoalType(weekly, GoalType.WEEKLY));
        return dailyExistItems;
    }

    public List<PlanItem> getDeletedItems(List<PlanItem> daily, List<PlanItem> weekly) {
        List<PlanItem> dailyDeletedItems = new ArrayList<>(getDeletedByGoalType(daily, GoalType.DAILY));
        dailyDeletedItems.addAll(getDeletedByGoalType(weekly, GoalType.WEEKLY));
        return dailyDeletedItems;
    }

    private List<PlanItem> getDeletedByGoalType(List<PlanItem> newItems, GoalType goalType) {
        Set<ActivityType> newActivities = extractActivityTypes(newItems, goalType);
        return this.planItems.stream()
                .filter((item) -> item.getGoalType().equals(goalType))
                .filter((item) -> !newActivities.contains(item.getActivityType()))
                .toList();
    }

    private List<PlanItem> getExistByGoalType(List<PlanItem> newItems, GoalType goalType) {
        Set<ActivityType> existActivities = extractActivityTypes(planItems, goalType);
        return newItems.stream()
                .filter(item -> existActivities.contains(item.getActivityType()))
                .toList();
    }

    private List<PlanItem> getNewByGoalType(List<PlanItem> newItems, GoalType goalType) {
        Set<ActivityType> existActivities = extractActivityTypes(planItems, goalType);
        return newItems.stream()
                .filter(item -> !existActivities.contains(item.getActivityType()))
                .toList();
    }

    private Set<ActivityType> extractActivityTypes(List<PlanItem> source, GoalType goalType) {
        return source.stream()
                .filter(item -> item.getGoalType().equals(goalType))
                .map(PlanItem::getActivityType)
                .collect(Collectors.toSet());
    }
}
