package com.jabiseo.domain.plan.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    boolean existsByCertificateAndMember(Certificate certificate, Member member);
    Optional<Plan> findFirstByCertificateAndMember(Certificate certificate, Member member);

    @Query("SELECT p FROM Plan p JOIN FETCH p.planItems WHERE p.id = :planId")
    Optional<Plan> findPlanWithItemsById(@Param("planId") Long planId);

}
