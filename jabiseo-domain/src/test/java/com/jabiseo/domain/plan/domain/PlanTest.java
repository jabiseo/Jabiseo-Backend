package com.jabiseo.domain.plan.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.plan.domain.ActivityType;
import com.jabiseo.domain.plan.domain.GoalType;
import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.domain.PlanItem;
import fixture.CertificateFixture;
import fixture.MemberFixture;
import fixture.PlanItemFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
        plan = new Plan(certificate, member, null, new ArrayList<>(originalItems));
    }

    @Test
    @DisplayName("기존 플랜 아이템 목록과 새 목록 중 기존에 없는 목록을 가져온다.")
    void getNewItemsSuccess() {
        //given
        // Time 이 리턴되어야 한다. (1개)
        List<PlanItem> newDailyItemsRequest = List.of(
                mockDailyItem(ActivityType.STUDY), // exist item
                mockDailyItem(ActivityType.EXAM), // exist item
                mockDailyItem(ActivityType.TIME) // new item
        );

        // Problem이 리턴되어야 한다 (1개)
        List<PlanItem> newWeeklyItemsRequest = List.of(
                mockWeeklyItem(ActivityType.EXAM), // exist item
                mockWeeklyItem(ActivityType.STUDY), // exist item
                mockWeeklyItem(ActivityType.PROBLEM) // new item
        );
        //when
        List<PlanItem> newItems = plan.getNewItems(newDailyItemsRequest, newWeeklyItemsRequest);

        //then
        assertThat(newItems.size()).isEqualTo(2);
        assertThat(newItems.stream()
                .anyMatch((item) -> item.getActivityType().equals(ActivityType.TIME)
                        && item.getGoalType().equals(GoalType.DAILY)))
                .isTrue();
        assertThat(newItems.stream()
                .anyMatch((item) -> item.getActivityType().equals(ActivityType.PROBLEM)
                        && item.getGoalType().equals(GoalType.WEEKLY)))
                .isTrue();
    }

    @Test
    @DisplayName("기존 플랜 아이템 목록과 새 목록 중 기존에 있는 아이템 목록을 가져온다.")
    void planExistingItems() {
        //given
        List<PlanItem> newDailyItemsRequest = List.of(
                mockDailyItem(ActivityType.STUDY), // exist item
                mockDailyItem(ActivityType.EXAM), // exist item
                mockDailyItem(ActivityType.TIME) // new item
        );
        List<PlanItem> newWeeklyItemsRequest = List.of(
                mockWeeklyItem(ActivityType.EXAM), // exist item
                mockWeeklyItem(ActivityType.STUDY), // exist item
                mockWeeklyItem(ActivityType.PROBLEM) // new item
        );
        //when
        List<PlanItem> newItems = plan.getExistItems(newDailyItemsRequest, newWeeklyItemsRequest);

        //then
        assertThat(newItems.size()).isEqualTo(4);
        assertThat(newItems.stream()
                .anyMatch((item) -> item.getActivityType().equals(ActivityType.STUDY)
                        && item.getGoalType().equals(GoalType.DAILY)))
                .isTrue();
        assertThat(newItems.stream()
                .anyMatch((item) -> item.getActivityType().equals(ActivityType.EXAM)
                        && item.getGoalType().equals(GoalType.DAILY)))
                .isTrue();
        assertThat(newItems.stream()
                .anyMatch((item) -> item.getActivityType().equals(ActivityType.STUDY)
                        && item.getGoalType().equals(GoalType.WEEKLY)))
                .isTrue();
        assertThat(newItems.stream()
                .anyMatch((item) -> item.getActivityType().equals(ActivityType.EXAM)
                        && item.getGoalType().equals(GoalType.WEEKLY)))
                .isTrue();
    }

    @Test
    @DisplayName("기존 플랜 아이템 목록을 가져올 시, PlanItem 의 target 값은 새로운 값으로 리턴된다.")
    void planExistingItemsTargetValueIsCorrect() {
        //given
        List<PlanItem> newDailyItemsRequest = List.of(
                new PlanItem(null, ActivityType.EXAM, GoalType.DAILY, 10)
        );
        List<PlanItem> newWeeklyItemsRequest = List.of();

        List<PlanItem> originalItems = List.of(new PlanItem(null, ActivityType.EXAM, GoalType.DAILY, 5));

        plan = new Plan(certificate, member, null, originalItems);

        //when
        List<PlanItem> newItems = plan.getExistItems(newDailyItemsRequest, newWeeklyItemsRequest);

        //then
        assertThat(newItems.size()).isEqualTo(1);
        assertThat(newItems.stream()
                .anyMatch((item) -> item.getActivityType().equals(ActivityType.EXAM)
                        && item.getGoalType().equals(GoalType.DAILY)))
                .isTrue();
        assertThat(newItems.get(0).getTargetValue()).isEqualTo(newDailyItemsRequest.get(0).getTargetValue());
    }

    @Test
    @DisplayName("기존 플랜 아이템 목록과 새 목록 중 삭제될 아이템 목록을 가져온다.")
    void planDeleteItems() {
        //given, 아래 6개의 아이템이 아닌 기존 목록의 item들 중 없는 값을 가져와야 한다.
        List<PlanItem> newDailyItemsRequest = List.of(
                mockDailyItem(ActivityType.STUDY), // exist item
                mockDailyItem(ActivityType.EXAM), // exist item
                mockDailyItem(ActivityType.TIME) // new item
        );
        List<PlanItem> newWeeklyItemsRequest = List.of(
                mockWeeklyItem(ActivityType.EXAM), // exist item
                mockWeeklyItem(ActivityType.STUDY), // exist item
                mockWeeklyItem(ActivityType.PROBLEM) // new item
        );
        // Problem이 삭제될 예정. (setUp)

        //when
        List<PlanItem> result = plan.getDeletedItems(newDailyItemsRequest, newWeeklyItemsRequest);

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.stream()
                .anyMatch((item) -> item.getActivityType().equals(ActivityType.PROBLEM)
                        && item.getGoalType().equals(GoalType.DAILY)))
                .isTrue();
    }

    @Test
    @DisplayName("플랜 수정시, 기존 플랜의 값이 추가되거나 삭제된다.")
    void modifyPlanTest() {
        //given
        List<PlanItem> newDailyItemsRequest = List.of(
                mockDailyItem(ActivityType.STUDY), // exist item
                mockDailyItem(ActivityType.EXAM), // exist item
                mockDailyItem(ActivityType.TIME) // new item
        );
        List<PlanItem> newWeeklyItemsRequest = List.of(
                mockWeeklyItem(ActivityType.EXAM), // exist item
                mockWeeklyItem(ActivityType.STUDY), // exist item
                mockWeeklyItem(ActivityType.PROBLEM) // new item
        );
        // Problem이 삭제될 예정. (setUp)
        int expectedSize = this.plan.getPlanItems().size() - 1 + 2;// -1(Problem), 2 => 위의 2개의 new items
        List<PlanItem> existItems = plan.getExistItems(newDailyItemsRequest, newWeeklyItemsRequest);
        List<PlanItem> newItems = plan.getNewItems(newDailyItemsRequest, newWeeklyItemsRequest);
        List<PlanItem> deletedItems = plan.getDeletedItems(newDailyItemsRequest, newWeeklyItemsRequest);

        //when
        plan.modifyPlanItems(existItems, newItems, deletedItems);
        List<PlanItem> planItems = plan.getPlanItems();

        //then
        assertThat(planItems.size()).isEqualTo(expectedSize);
    }

    private PlanItem mockDailyItem(ActivityType type) {
        return PlanItemFixture.createPlanItem(type, GoalType.DAILY);
    }

    private PlanItem mockWeeklyItem(ActivityType type) {
        return PlanItemFixture.createPlanItem(type, GoalType.WEEKLY);
    }
}
