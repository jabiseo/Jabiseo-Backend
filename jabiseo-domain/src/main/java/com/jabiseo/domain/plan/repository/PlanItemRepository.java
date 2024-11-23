package com.jabiseo.domain.plan.repository;

import com.jabiseo.domain.plan.domain.PlanItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanItemRepository extends JpaRepository<PlanItem, Long> {
}
