package com.jabiseo.domain.plan.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.plan.exception.PlanErrorCode;
import com.jabiseo.domain.plan.exception.PlanBusinessException;
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
import java.util.*;

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


    @Embedded
    private PlanItemGroup planItemGroup;


    public Plan(Certificate certificate, Member member, LocalDate endAt) {
        this.certificate = certificate;
        this.member = member;
        this.endAt = endAt;
    }

    @Builder
    public Plan(Certificate certificate, Member member, LocalDate endAt, PlanItemGroup planItemGroup) {
        this.certificate = certificate;
        this.member = member;
        this.endAt = endAt;
        this.planItemGroup = planItemGroup;
    }

    public static Plan create(Member member, LocalDate endAt) {
        return Plan.builder()
                .member(member)
                .endAt(endAt)
                .build();
    }


    public void modify(List<PlanItem> items, LocalDate endAt) {
        if(!endAt.equals(this.endAt)){
            this.endAt = endAt;

        planItemGroup.modifyPlanItems(items);}
    }



    public void checkOwner(Long memberId) {
        if (!memberId.equals(this.member.getId())) {
            throw new PlanBusinessException(PlanErrorCode.IS_NOT_OWNER);
        }
    }

    public void updatePlanItemGroup(PlanItemGroup planItemGroup) {
        this.planItemGroup = planItemGroup;
    }

}
