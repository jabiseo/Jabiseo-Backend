package com.jabiseo.domain.plan.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.plan.exception.PlanErrorCode;
import com.jabiseo.domain.plan.exception.PlanBusinessException;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plan {

    @Id
    @Tsid
    @Column(name = "plan_id")
    private Long id;

    private LocalDate endAt;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Certificate certificate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<PlanItem> planItems = new ArrayList<>();

    public Plan(Certificate certificate, Member member, LocalDate endAt) {
        this.certificate = certificate;
        this.member = member;
        this.endAt = endAt;
    }

    public Plan(Certificate certificate, Member member, LocalDate endAt, List<PlanItem> planItems) {
        this.certificate = certificate;
        this.member = member;
        this.endAt = endAt;
        this.planItems = planItems;
    }

    public List<PlanItem> getNewItems(List<PlanItem> daily, List<PlanItem> weekly) {
        Set<ActivityType> originDailyItemActivities = extractActivityTypes(this.planItems, GoalType.DAILY);
        Set<ActivityType> originWeeklyItemActivities = extractActivityTypes(this.planItems, GoalType.WEEKLY);

        List<PlanItem> newDailyItems = filterNewItems(daily, originDailyItemActivities);
        List<PlanItem> newWeeklyItems = filterNewItems(weekly, originWeeklyItemActivities);

        List<PlanItem> result = new ArrayList<>(newDailyItems);
        result.addAll(newWeeklyItems);
        return Collections.unmodifiableList(result);
    }

    public List<PlanItem> getExistItems(List<PlanItem> daily, List<PlanItem> weekly) {
        Set<ActivityType> originDailyItemActivities = extractActivityTypes(this.planItems, GoalType.DAILY);
        Set<ActivityType> originWeeklyItemActivities = extractActivityTypes(this.planItems, GoalType.WEEKLY);

        List<PlanItem> result = new ArrayList<>(filterExistingItems(daily, originDailyItemActivities));
        result.addAll(filterExistingItems(weekly, originWeeklyItemActivities));
        return Collections.unmodifiableList(result);
    }

    public List<PlanItem> getDeletedItems(List<PlanItem> daily, List<PlanItem> weekly) {
        List<PlanItem> result = new ArrayList<>(filterDeleteItems(this.planItems, daily, GoalType.DAILY));
        result.addAll(filterDeleteItems(this.planItems, weekly, GoalType.WEEKLY));
        return Collections.unmodifiableList(result);
    }

    public List<PlanItem> filterDeleteItems(List<PlanItem> original, List<PlanItem> newItem, GoalType goalType) {
        Set<ActivityType> newItemsType = extractActivityTypes(newItem, goalType);
        return original.stream()
                .filter((item) -> item.getGoalType().equals(goalType))
                .filter((item) -> !newItemsType.contains(item.getActivityType()))
                .toList();
    }

    private List<PlanItem> filterExistingItems(List<PlanItem> items, Set<ActivityType> existingActivities) {
        return items.stream()
                .filter(item -> existingActivities.contains(item.getActivityType()))
                .toList();
    }

    private List<PlanItem> filterNewItems(List<PlanItem> items, Set<ActivityType> existingActivities) {
        return items.stream()
                .filter(item -> !existingActivities.contains(item.getActivityType()))
                .toList();
    }

    private Set<ActivityType> extractActivityTypes(List<PlanItem> source, GoalType goalType) {
        return source.stream()
                .filter(item -> item.getGoalType().equals(goalType))
                .map(PlanItem::getActivityType)
                .collect(Collectors.toSet());
    }


    public void modifyPlanItems(List<PlanItem> existItems, List<PlanItem> newItems, List<PlanItem> deletedItems) {

        Map<String, PlanItem> planItemMap = this.planItems.stream()
                .collect(Collectors.toMap(
                        item -> planItemTypeAndGoalKey(item.getActivityType(), item.getGoalType()),
                        item -> item
                ));


        // 기존 플랜 값 수정
        existItems.forEach(existItem -> {
            String key = planItemTypeAndGoalKey(existItem.getActivityType(), existItem.getGoalType());
            if (planItemMap.containsKey(key)) {
                planItemMap.get(key).updateTargetValue(existItem.getTargetValue());
            }
        });

        // 삭제될 플랜은 삭제
        List<PlanItem> removeItems = deletedItems.stream()
                .map(deletedItem -> planItemMap.get(planItemTypeAndGoalKey(deletedItem.getActivityType(), deletedItem.getGoalType())))
                .filter(Objects::nonNull)
                .toList();
        this.planItems.removeAll(removeItems);

        // 새 플랜은 추가
        this.planItems.addAll(newItems);
    }

    private String planItemTypeAndGoalKey(ActivityType activityType, GoalType goalType) {
        return activityType.name() + "-" + goalType.name();
    }


    public void modifyEndAt(LocalDate endAt) {
        if (!endAt.equals(this.endAt)) {
            this.endAt = endAt;
        }
    }

    public static Plan create(Member member, LocalDate endAt) {
        return new Plan(member.getCurrentCertificate(), member, endAt);
    }

    public void checkOwner(Long memberId) {
        if (!memberId.equals(this.member.getId())) {
            throw new PlanBusinessException(PlanErrorCode.IS_NOT_OWNER);
        }
    }
}
