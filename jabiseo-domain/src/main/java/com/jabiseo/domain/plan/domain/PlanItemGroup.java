package com.jabiseo.domain.plan.domain;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public class PlanItemGroup {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<PlanItem> planItems = new ArrayList<>();

    public PlanItemGroup(List<PlanItem> planItems) {
        this.planItems = new ArrayList<>(planItems);
    }

    public PlanItemGroup() {
    }

    public void modifyPlanItems(List<PlanItem> items) {
        planItems.removeIf(planItem -> items.stream().noneMatch((requestItem) -> requestItem.equalsItem(planItem)));

        planItems.forEach(planItem -> updatePlanItemTargetValue(planItem, items));

        List<PlanItem> newItems = items.stream()
                .filter(item -> planItems.stream().noneMatch((currentItem) -> currentItem.equalsItem(item)))
                .toList();
        planItems.addAll(newItems);
    }
    public boolean hasDailyItems() {
        return planItems.stream().anyMatch((item) -> item.getGoalType().equals(GoalType.DAILY));
    }

    public boolean hasWeeklyItems() {
        return planItems.stream().anyMatch((item) -> item.getGoalType().equals(GoalType.WEEKLY));
    }


    public List<PlanItem> getPlanItems() {
        return planItems;
    }

    private void updatePlanItemTargetValue(PlanItem planItem, List<PlanItem> items) {
        items.stream()
                .filter(item -> item.equalsItem(planItem))
                .findFirst()
                .ifPresent(matchingItem -> planItem.updateTargetValue(matchingItem.getTargetValue()));
    }



}
