package com.jabiseo.domain.plan.domain;

import fixture.PlanItemFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PlanItemGroupTest {

    List<PlanItem> existingItems;

    @BeforeEach
    void setUp() throws Exception {
        existingItems = List.of(
                mockDailyItem(ActivityType.STUDY),
                mockDailyItem(ActivityType.EXAM),
                mockDailyItem(ActivityType.PROBLEM),
                mockWeeklyItem(ActivityType.STUDY),
                mockWeeklyItem(ActivityType.EXAM)
        );
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

        PlanItemGroup group = new PlanItemGroup(existingItems);

        //when
        group.modifyPlanItems(itemsRequest);
        List<PlanItem> planItems = group.getPlanItems();

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
