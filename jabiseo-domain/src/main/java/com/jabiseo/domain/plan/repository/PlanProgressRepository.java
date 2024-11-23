package com.jabiseo.domain.plan.repository;

import com.jabiseo.domain.plan.domain.GoalType;
import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.domain.PlanProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PlanProgressRepository extends JpaRepository<PlanProgress, Long> {

    List<PlanProgress> findAllByPlanAndProgressDateBetweenOrderByProgressDate(Plan plan, LocalDate start, LocalDate end);

    List<PlanProgress> findAllByPlanAndProgressDateBetweenAndGoalType(Plan plan, LocalDate start, LocalDate end, GoalType goalType);

    @Modifying
    @Query("DELETE FROM PlanProgress pp WHERE pp.plan.id = :planId")
    void deleteByPlanId(Long planId);
}
