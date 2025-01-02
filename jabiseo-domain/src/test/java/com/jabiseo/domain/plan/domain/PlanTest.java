package com.jabiseo.domain.plan.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import fixture.CertificateFixture;
import fixture.MemberFixture;
import fixture.PlanItemFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("플랜 Entity 테스트")
class PlanTest {

    Plan plan;
    Certificate certificate; // setUp시 고정
    Member member; // setUp시 고정

    @BeforeEach
    void setUp() throws Exception {
        certificate = CertificateFixture.createCertificate();
        member = MemberFixture.createMember();

        List<PlanItem> originalItems = List.of(
                mockDailyItem(ActivityType.STUDY),
                mockDailyItem(ActivityType.EXAM),
                mockDailyItem(ActivityType.PROBLEM),
                mockWeeklyItem(ActivityType.STUDY),
                mockWeeklyItem(ActivityType.EXAM)
        );
        plan = new Plan(certificate, member, null, new PlanItemGroup(new ArrayList<>(originalItems)));
    }

    @Test
    @DisplayName("플랜 수정시, 기존 플랜의 값이 추가되거나 삭제된다.")
    void modifyPlanTest() {
        //given
        List<PlanItem> itemsRequest = List.of(
                mockDailyItem(ActivityType.STUDY), // exist item
                mockDailyItem(ActivityType.EXAM), // exist item
                mockDailyItem(ActivityType.TIME), // new item
                mockWeeklyItem(ActivityType.EXAM), // exist item
                mockWeeklyItem(ActivityType.STUDY), // exist item
                mockWeeklyItem(ActivityType.PROBLEM) // new item
        );


        //when
        plan.modify(itemsRequest, LocalDate.now());
        List<PlanItem> planItems = plan.getPlanItemGroup().getPlanItems();

        //then
        assertThat(planItems.size()).isEqualTo(6);
    }

    private PlanItem mockDailyItem(ActivityType type) {
        return PlanItemFixture.createPlanItem(type, GoalType.DAILY);
    }

    private PlanItem mockWeeklyItem(ActivityType type) {
        return PlanItemFixture.createPlanItem(type, GoalType.WEEKLY);
    }
}
