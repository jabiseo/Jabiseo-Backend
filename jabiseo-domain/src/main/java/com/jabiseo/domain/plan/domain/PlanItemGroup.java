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
        List<PlanItem> newItems = getNewItems(items);
        List<PlanItem> existItems = getExistItems(items);
        List<PlanItem> deletedItems = getDeletedItems(items);

        // 삭제될 아이템 삭제
        planItems.removeIf(planItem -> deletedItems.stream().anyMatch((item) -> item.equalsItems(planItem)));

        // 이미 존재하는 items의 target value 수정 N*M
        planItems.forEach((planItem -> planItem.updateTargetValue(findTargetValue(existItems, planItem))));

        // 새 아이템 추가
        planItems.addAll(newItems);
    }


    public List<PlanItem> getPlanItems() {
        return planItems;
    }

    public List<PlanItem> getNewItems(List<PlanItem> inputItems) {
        return inputItems.stream()// 새로 들어온 아이템들 중
                .filter(item -> planItems.stream().noneMatch((currentItem) -> currentItem.equalsItems(item))) // 이미 있는 아이템과 매칭되지 않는 것
                .toList();
    }

    public List<PlanItem> getExistItems(List<PlanItem> inputItems) {
        return inputItems.stream()// 새로 들어온 아이템들 중
                .filter(item -> planItems.stream().anyMatch((currentItem) -> currentItem.equalsItems(item))) // 이미 있는 아이템과 매칭되는것
                .toList();
    }

    public List<PlanItem> getDeletedItems(List<PlanItem> inputItems) {
        return this.planItems.stream() // 기존 아이템들 중
                .filter(item -> inputItems.stream().noneMatch((inputItem) -> inputItem.equalsItems(item))) // 새로 들어온 아이템과 매칭되지 않는 것.
                .toList();
    }

    private int findTargetValue(List<PlanItem> source, PlanItem target) {
        PlanItem find = source.stream()
                .filter((item) -> item.equalsItems(target))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Target value not found"));
        return find.getTargetValue();
    }


}
