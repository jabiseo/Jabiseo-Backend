package com.jabiseo.domain.plan.repository;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.plan.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    boolean existsByCertificateAndMember(Certificate certificate, Member member);

    Optional<Plan> findFirstByCertificateAndMember(Certificate certificate, Member member);

    @Query("SELECT p FROM Plan p JOIN FETCH p.planItemGroup JOIN FETCH p.certificate WHERE p.certificate = :certificate AND p.member = :member")
    Optional<Plan> findFirstByCertificateAndMemberWithPlanItems(Certificate certificate, Member member);

    @Query("SELECT p FROM Plan p JOIN FETCH p.planItemGroup WHERE p.id = :planId")
    Optional<Plan> findPlanWithItemsById(@Param("planId") Long planId);

    @Query("SELECT p FROM Plan p JOIN FETCH p.member WHERE p.id = :planId")
    Optional<Plan> findByIdWithMember(@Param("planId") Long planId);
}
