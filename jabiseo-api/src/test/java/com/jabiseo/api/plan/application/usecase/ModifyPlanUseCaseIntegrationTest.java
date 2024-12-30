package com.jabiseo.api.plan.application.usecase;

import com.jabiseo.api.plan.dto.request.ModifyPlanRequest;
import com.jabiseo.api.plan.dto.request.PlanItemRequest;
import com.jabiseo.domain.plan.domain.*;
import com.jabiseo.domain.plan.repository.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.*;

@DisplayName("modify plan usecase 통합 테스트")
@SpringBootTest
@ActiveProfiles("test")
@SqlGroup({
        @Sql(value = "/sql/modify-plan-usecase-test-data.sql", executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = AFTER_TEST_METHOD)
})
class ModifyPlanUseCaseIntegrationTest {

    @Autowired
    private ModifyPlanUseCase modifyPlanUseCase;

    @Autowired
    private PlanRepository planRepository;

    List<PlanItemRequest> existPlanItems = new ArrayList<>();

    @BeforeEach
    void init() {
        PlanItemRequest request1 = PlanItemRequest.builder()
                .targetValue(100)
                .activityType("EXAM")
                .build();
        PlanItemRequest request2 = PlanItemRequest.builder()
                .targetValue(100)
                .activityType("STUDY")
                .build();
        PlanItemRequest request3 = PlanItemRequest.builder()
                .targetValue(100)
                .activityType("TIME")
                .build();
        existPlanItems.addAll(List.of(request1, request2, request3));

    }


    @Test
    @DisplayName("새 Item을 추가한 후 요청하면 새 PlanItem 을 추가할 수 있다.")
    void addNewPlanItem() {
        //given
        List<PlanItemRequest> dailyItems = new ArrayList<>(existPlanItems);
        dailyItems.add(PlanItemRequest.builder()
                .targetValue(100)
                .activityType("PROBLEM")
                .build());
        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), dailyItems, Collections.emptyList());

        //when
        modifyPlanUseCase.execute(1L, 1L, request);

        //then
        List<PlanItem> savedPlanItems = planRepository.findPlanWithItemsById(1L).get().getPlanItemGroup().getPlanItems();

        Optional<PlanItem> result = savedPlanItems.stream().filter(item ->
                        Objects.equals(item.getActivityType(), ActivityType.PROBLEM) &&
                                Objects.equals(item.getGoalType(), GoalType.DAILY))
                .findAny();

        assertThat(result).isNotEmpty();
        assertThat(result.get().getActivityType()).isEqualTo(ActivityType.PROBLEM);
        assertThat(result.get().getTargetValue()).isEqualTo(100L);
    }

    @Test
    @DisplayName("기존 아이템에서 제외하고 요청하면 해당 Item이 삭제된다 ")
    void removePlanItems() {
        //given
        PlanItemRequest removedItem = existPlanItems.remove(0);
        ActivityType removedItemType = ActivityType.valueOf(removedItem.activityType());
        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), existPlanItems, Collections.emptyList());

        //when
        modifyPlanUseCase.execute(1L, 1L, request);

        //then
        List<PlanItem> savedPlanItems = planRepository.findPlanWithItemsById(1L).get().getPlanItemGroup().getPlanItems();
        Optional<PlanItem> result = savedPlanItems.stream().filter(item ->
                        Objects.equals(item.getActivityType(), removedItemType) &&
                                Objects.equals(item.getGoalType(), GoalType.DAILY))
                .findAny();

        assertThat(savedPlanItems.size()).isEqualTo(2);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("기존 아이템에서 목표 값을 수정하면 수정한 결과가 반영된다 .")
    void modifyPlanItems() {
        //given
        existPlanItems.set(0, PlanItemRequest.builder()
                .activityType(existPlanItems.get(0).activityType())
                .targetValue(50)
                .build());

        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), existPlanItems, Collections.emptyList());

        //when
        modifyPlanUseCase.execute(1L, 1L, request);

        //then
        List<PlanItem> savedPlanItems = planRepository.findPlanWithItemsById(1L).get().getPlanItemGroup().getPlanItems();
        Optional<PlanItem> result = savedPlanItems.stream().filter(item ->
                        Objects.equals(item.getActivityType(), ActivityType.valueOf(existPlanItems.get(0).activityType())) &&
                                Objects.equals(item.getGoalType(), GoalType.DAILY))
                .findAny();

        assertThat(savedPlanItems.size()).isEqualTo(3);
        assertThat(result).isNotEmpty();
        assertThat(result.get().getTargetValue()).isEqualTo(50);
    }


}
