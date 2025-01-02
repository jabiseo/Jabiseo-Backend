package com.jabiseo.domain.plan.domain;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanProgress {

    @Id
    @Tsid
    @Column(name = "plan_progress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Plan plan;

    private LocalDate progressDate;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    private Integer targetValue;

    private Long completedValue;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public PlanProgress(Plan plan, LocalDate progressDate, ActivityType activityType, GoalType goalType, Integer targetValue, Long completedValue) {
        this.plan = plan;
        this.progressDate = progressDate;
        this.activityType = activityType;
        this.goalType = goalType;
        this.targetValue = targetValue;
        this.completedValue = completedValue;
    }

    public void addCompletedValue(Long add) {
        this.completedValue += add;
    }

    public void updateTargetValue(Integer value) {
        this.targetValue = value;
    }

    public boolean equalsProgress(PlanProgress other) {
        if (other == null) return false;
        return this.goalType.equals(other.getGoalType()) && this.activityType.equals(other.getActivityType());
    }

    public boolean equalsItems(PlanItem otherItems){
        if (otherItems == null) return false;
        return this.goalType.equals(otherItems.getGoalType()) && this.activityType.equals(otherItems.getActivityType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlanProgress that = (PlanProgress) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
