package com.jabiseo.domain.plan.service;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.learning.domain.LearningMode;
import com.jabiseo.domain.learning.dto.LearningWithSolvingCountQueryDto;
import com.jabiseo.domain.learning.repository.LearningRepository;
import com.jabiseo.domain.plan.domain.*;
import com.jabiseo.domain.plan.repository.PlanProgressRepository;
import fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlanProgressCreateServiceTest {

    @InjectMocks
    PlanProgressCreateService planProgressCreateService;

    @Mock
    LearningRepository learningRepository;

    @Mock
    PlanProgressRepository planProgressRepository;

    WeeklyDefineStrategy weeklyDefineStrategy = new SundayStartWeeklyStrategy();

    @Test
    @DisplayName("create 호출 시 learning 기록이 있다면 가져와 반영후 저장한다.")
    void createAndCalculateProgress() throws Exception {
        //given
        PlanProgressGroup newGroup = new PlanProgressGroup(
                List.of(
                        PlanProgress.builder().activityType(ActivityType.EXAM).goalType(GoalType.DAILY).completedValue(0L).build(),
                        PlanProgress.builder().activityType(ActivityType.STUDY).goalType(GoalType.DAILY).completedValue(0L).build(),
                        PlanProgress.builder().activityType(ActivityType.PROBLEM).goalType(GoalType.DAILY).completedValue(0L).build(),
                        PlanProgress.builder().activityType(ActivityType.TIME).goalType(GoalType.DAILY).completedValue(0L).build()
                ),
                weeklyDefineStrategy
        );

        Plan plan = Plan.builder()
                .member(MemberFixture.createMember())
                .certificate(Certificate.of("정처기"))
                .build();
        List<LearningWithSolvingCountQueryDto> queryResult = List.of(
                LearningWithSolvingCountQueryDto.builder().solvingCount(10L).learningTime(10L).mode(LearningMode.EXAM).build(),
                LearningWithSolvingCountQueryDto.builder().solvingCount(10L).learningTime(10L).mode(LearningMode.EXAM).build(),
                LearningWithSolvingCountQueryDto.builder().solvingCount(10L).learningTime(10L).mode(LearningMode.STUDY).build()
        );
        given(learningRepository
                .findLearningWithSolvingCount(plan.getMember(),
                        plan.getCertificate(),
                        newGroup.getWeekPeriod(LocalDate.now()).getStart(),
                        newGroup.getWeekPeriod(LocalDate.now()).getEnd()))
                .willReturn(queryResult);


        //when
        planProgressCreateService.create(newGroup, plan);

        //then
        ArgumentCaptor<List<PlanProgress>> captor = ArgumentCaptor.forClass(List.class);
        verify(planProgressRepository, times(1)).saveAll(captor.capture());

        List<PlanProgress> result = captor.getValue();
        assertThat(result).hasSize(4)
                .extracting((item) -> tuple(item.getActivityType(), item.getGoalType(), item.getCompletedValue()))
                .containsExactlyInAnyOrder(
                        tuple(ActivityType.EXAM, GoalType.DAILY, 2L),
                        tuple(ActivityType.STUDY, GoalType.DAILY, 1L),
                        tuple(ActivityType.PROBLEM, GoalType.DAILY, 30L),
                        tuple(ActivityType.TIME, GoalType.DAILY, 30L)
                );
    }
}
