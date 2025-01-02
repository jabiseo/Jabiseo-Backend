package com.jabiseo.domain.plan.service;

import com.jabiseo.domain.plan.domain.*;
import com.jabiseo.domain.plan.repository.PlanProgressRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlanProgressServiceTest {


    @InjectMocks
    PlanProgressService planProgressService;

    @Mock
    WeeklyDefineStrategy weeklyDefineStrategy;

    @Mock
    PlanProgressCreateService planProgressCreateService;

    @Mock
    PlanProgressRepository planProgressRepository;

    @Mock
    PlanProgressGroupFactory planProgressGroupFactory;


    @Test
    @DisplayName("update 호출 시 수정될 값들을 수정하고 저장한다. ")
    void updateAndModifyTargetValue() {
        //given
        Plan plan = Plan.builder().build();
        PlanProgressGroup groupWithInitdata = new PlanProgressGroup(List.of(
                PlanProgress.builder().activityType(ActivityType.EXAM).goalType(GoalType.DAILY).targetValue(10).build(),
                PlanProgress.builder().activityType(ActivityType.STUDY).goalType(GoalType.DAILY).targetValue(10).build() // removed
        ), weeklyDefineStrategy);

        given(planProgressGroupFactory.createWithInitialData(plan)).willReturn(groupWithInitdata);
        List<PlanItem> inputPlanItems = List.of(
                PlanItem.builder().activityType(ActivityType.EXAM).goalType(GoalType.DAILY).targetValue(20).build(),// exist
                PlanItem.builder().activityType(ActivityType.TIME).goalType(GoalType.DAILY).targetValue(20).build() // newItem
        );


        //when
        planProgressService.update(plan, inputPlanItems);

        //then
        ArgumentCaptor<List<PlanProgress>> captor = ArgumentCaptor.forClass(List.class);
        verify(planProgressRepository, times(1)).saveAll(captor.capture());
        List<PlanProgress> savedValues = captor.getValue();
        assertThat(savedValues).hasSize(1);
        assertThat(savedValues.get(0).getActivityType()).isEqualTo(ActivityType.EXAM);
        assertThat(savedValues.get(0).getGoalType()).isEqualTo(GoalType.DAILY);
        assertThat(savedValues.get(0).getTargetValue()).isEqualTo(20);
    }

    @Test
    @DisplayName("update 호출 시 삭제 될 Progress들을 삭제한다. ")
    void updateAndRemoveProgress() {
        //given
        Plan plan = Plan.builder().build();
        PlanProgressGroup groupWithInitdata = new PlanProgressGroup(List.of(
                PlanProgress.builder().activityType(ActivityType.EXAM).goalType(GoalType.DAILY).targetValue(10).build(),
                PlanProgress.builder().activityType(ActivityType.STUDY).goalType(GoalType.DAILY).targetValue(10).build() // removed
        ), weeklyDefineStrategy);

        given(planProgressGroupFactory.createWithInitialData(plan)).willReturn(groupWithInitdata);
        List<PlanItem> inputPlanItems = List.of(
                PlanItem.builder().activityType(ActivityType.EXAM).goalType(GoalType.DAILY).targetValue(20).build(),// exist
                PlanItem.builder().activityType(ActivityType.TIME).goalType(GoalType.DAILY).targetValue(20).build() // newItem
        );


        //when
        planProgressService.update(plan, inputPlanItems);

        //then
        ArgumentCaptor<List<PlanProgress>> captor = ArgumentCaptor.forClass(List.class);
        verify(planProgressRepository, times(1)).deleteAll(captor.capture());
        List<PlanProgress> savedValues = captor.getValue();
        assertThat(savedValues).hasSize(1);
        assertThat(savedValues.get(0).getActivityType()).isEqualTo(ActivityType.STUDY);
        assertThat(savedValues.get(0).getGoalType()).isEqualTo(GoalType.DAILY);
    }


    @Test
    @DisplayName("update 호출 시 추가될 값들은 CreateService에 위임한다. ")
    void updateAndNewItemDelegatingCreateService() {
        //given
        Plan plan = Plan.builder().build();
        PlanProgressGroup groupWithInitdata = new PlanProgressGroup(List.of(
                PlanProgress.builder().activityType(ActivityType.EXAM).goalType(GoalType.DAILY).targetValue(10).build(),
                PlanProgress.builder().activityType(ActivityType.STUDY).goalType(GoalType.DAILY).targetValue(10).build() // removed
        ), weeklyDefineStrategy);

        given(planProgressGroupFactory.createWithInitialData(plan)).willReturn(groupWithInitdata);
        List<PlanItem> inputPlanItems = List.of(
                PlanItem.builder().activityType(ActivityType.EXAM).goalType(GoalType.DAILY).targetValue(20).build(),// exist
                PlanItem.builder().activityType(ActivityType.TIME).goalType(GoalType.DAILY).targetValue(20).build() // newItem
        );


        //when
        planProgressService.update(plan, inputPlanItems);

        //then
        ArgumentCaptor<PlanProgressGroup> captor = ArgumentCaptor.forClass(PlanProgressGroup.class);
        verify(planProgressCreateService, times(1)).create(captor.capture(), eq(plan));
        PlanProgressGroup result = captor.getValue();
        assertThat(result.getProgresses()).hasSize(1);
        assertThat(result.getProgresses().get(0).getActivityType()).isEqualTo(ActivityType.TIME);
        assertThat(result.getProgresses().get(0).getGoalType()).isEqualTo(GoalType.DAILY);
    }
}
