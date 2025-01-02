package com.jabiseo.domain.plan.domain;

import com.jabiseo.domain.learning.domain.LearningMode;
import com.jabiseo.domain.plan.dto.LearningResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

class PlanProgressGroupTest {

    private final WeeklyDefineStrategy weeklyDefineStrategy = new SundayStartWeeklyStrategy();

    @Test
    @DisplayName("findNew 호출 시 Item 목록을 기반으로 새 Progress를 리턴한다")
    void findNewTest() {
        //given
        List<PlanProgress> init = List.of(
                PlanProgress.builder().activityType(ActivityType.EXAM).goalType(GoalType.DAILY).build(),
                PlanProgress.builder().activityType(ActivityType.STUDY).goalType(GoalType.DAILY).build(),
                PlanProgress.builder().activityType(ActivityType.PROBLEM).goalType(GoalType.DAILY).build()
        );
        PlanProgressGroup progressGroup = new PlanProgressGroup(init, weeklyDefineStrategy);
        List<PlanItem> items = List.of(
                PlanItem.builder().activityType(ActivityType.EXAM).goalType(GoalType.DAILY).build(),
                PlanItem.builder().activityType(ActivityType.STUDY).goalType(GoalType.DAILY).build(),
                PlanItem.builder().activityType(ActivityType.PROBLEM).goalType(GoalType.DAILY).build(),
                PlanItem.builder().activityType(ActivityType.TIME).goalType(GoalType.DAILY).build(),
                PlanItem.builder().activityType(ActivityType.EXAM).goalType(GoalType.WEEKLY).build()
        );

        //when

        PlanProgressGroup newGroup = progressGroup.findNew(items);
        List<PlanProgress> result = newGroup.getProgresses();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting("activityType").containsExactlyInAnyOrder(ActivityType.TIME, ActivityType.EXAM);
        assertThat(result).extracting("goalType").containsExactlyInAnyOrder(GoalType.DAILY, GoalType.WEEKLY);
    }

    @Test
    @DisplayName("findRemoved 호출시 기존 객체의 값들 중 Item 목록에 없는 (삭제될) 목록을 포함해 리턴한다.")
    void findRemovedTest() {
        //given
        List<PlanProgress> init = List.of(
                PlanProgress.builder().activityType(ActivityType.EXAM).goalType(GoalType.DAILY).build(), // removed
                PlanProgress.builder().activityType(ActivityType.STUDY).goalType(GoalType.DAILY).build(),
                PlanProgress.builder().activityType(ActivityType.PROBLEM).goalType(GoalType.DAILY).build()
        );
        PlanProgressGroup progressGroup = new PlanProgressGroup(init, weeklyDefineStrategy);
        List<PlanItem> items = List.of(
                PlanItem.builder().activityType(ActivityType.STUDY).goalType(GoalType.DAILY).build(), // exist
                PlanItem.builder().activityType(ActivityType.PROBLEM).goalType(GoalType.DAILY).build(), // exist
                PlanItem.builder().activityType(ActivityType.TIME).goalType(GoalType.DAILY).build(), // new
                PlanItem.builder().activityType(ActivityType.EXAM).goalType(GoalType.WEEKLY).build() // new
        );

        //when
        List<PlanProgress> result = progressGroup.findRemoved(items).getProgresses();


        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).extracting("activityType").containsExactlyInAnyOrder(ActivityType.EXAM);
        assertThat(result).extracting("goalType").containsExactlyInAnyOrder(GoalType.DAILY);
    }

    @Test
    @DisplayName("update 호출 시 기존 객체와 같은 경우 target 값을 수정 해 리턴한다 ")
    void updateTest(){
        //given
        List<PlanProgress> init = List.of(
                PlanProgress.builder().activityType(ActivityType.EXAM).goalType(GoalType.DAILY).targetValue(1).build(),
                PlanProgress.builder().activityType(ActivityType.STUDY).goalType(GoalType.DAILY).targetValue(1).build(),
                PlanProgress.builder().activityType(ActivityType.PROBLEM).goalType(GoalType.DAILY).targetValue(1).build()
        );
        PlanProgressGroup progressGroup = new PlanProgressGroup(init, weeklyDefineStrategy);
        List<PlanItem> items = List.of(
                PlanItem.builder().activityType(ActivityType.EXAM).goalType(GoalType.DAILY).targetValue(3).build(), // exist
                PlanItem.builder().activityType(ActivityType.STUDY).goalType(GoalType.DAILY).targetValue(4).build(), // exist
                PlanItem.builder().activityType(ActivityType.PROBLEM).goalType(GoalType.DAILY).targetValue(5).build() // exist
        );

        //when
        List<PlanProgress> result = progressGroup.update(items).getProgresses();


        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).containsExactlyInAnyOrder(
                PlanProgress.builder().activityType(ActivityType.EXAM).goalType(GoalType.DAILY).targetValue(3).build(),
                PlanProgress.builder().activityType(ActivityType.STUDY).goalType(GoalType.DAILY).targetValue(4).build(),
                PlanProgress.builder().activityType(ActivityType.PROBLEM).goalType(GoalType.DAILY).targetValue(5).build()
        );
    }

    @Test
    @DisplayName("calculate 호출 시 LearnResult에 따라 Plan Progress 값을 변경해 리턴한다.")
    void calculateTest(){
        //given
        List<PlanProgress> initialProgresses = List.of(
                PlanProgress.builder().activityType(ActivityType.EXAM).completedValue(0L).goalType(GoalType.DAILY).build(),
                PlanProgress.builder().activityType(ActivityType.STUDY).completedValue(0L).goalType(GoalType.DAILY).build(),
                PlanProgress.builder().activityType(ActivityType.PROBLEM).completedValue(0L).goalType(GoalType.DAILY).build(),
                PlanProgress.builder().activityType(ActivityType.TIME).completedValue(0L).goalType(GoalType.DAILY).build()
        );
        PlanProgressGroup progressGroup = new PlanProgressGroup(initialProgresses, weeklyDefineStrategy);

        List<LearningResult> learningResults = List.of(
                LearningResult.builder().mode(LearningMode.EXAM).learningTime(10L).solvingCount(5L).build(),
                LearningResult.builder().mode(LearningMode.STUDY).learningTime(20L).solvingCount(3L).build(),
                LearningResult.builder().mode(LearningMode.EXAM).learningTime(5L).solvingCount(2L).build(),
                LearningResult.builder().mode(LearningMode.STUDY).learningTime(15L).solvingCount(1L).build()
        );
        //when

        List<PlanProgress> progresses = progressGroup.calculate(learningResults)
                .getProgresses();

        //then
        assertThat(progresses)
                .hasSize(4)
                .extracting(PlanProgress::getActivityType, PlanProgress::getCompletedValue)
                .containsExactlyInAnyOrder(
                        tuple(ActivityType.EXAM, 2L), // exam 2개
                        tuple(ActivityType.STUDY, 2L), // study 2개
                        tuple(ActivityType.PROBLEM, 11L), // 총 훙 문제 수 11개
                        tuple(ActivityType.TIME, 50L) //  총 푼 시간 40
                );
    }
}
