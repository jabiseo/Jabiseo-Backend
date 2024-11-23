package com.jabiseo.domain.plan.service;

import com.jabiseo.domain.learning.domain.LearningMode;
import com.jabiseo.domain.learning.repository.LearningRepository;
import com.jabiseo.domain.learning.dto.LearningWithSolvingCountQueryDto;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.plan.domain.*;
import com.jabiseo.domain.plan.repository.PlanProgressRepository;
import fixture.MemberFixture;
import fixture.PlanFixture;
import fixture.PlanItemFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fixture.PlanProgressFixture.createPlanProgress;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlanProgressServiceTest {

    @InjectMocks
    private PlanProgressService planProgressService;
    @Mock
    private PlanProgressRepository planProgressRepository;
    @Mock
    private LearningRepository learningRepository;

    @Mock
    private WeeklyDefineStrategy weeklyDefineStrategy;

    private Member member;

    @BeforeEach
    void setUp() {
        member = MemberFixture.createMember();
    }

    @Test
    @DisplayName("정상 동작 테스트, 주간  일간에 맞게 2번 호출한다")
    void successCallSaveAllTwo() {
        //given
        List<PlanItem> planItems = Arrays.asList(
                PlanItemFixture.createPlanItem(ActivityType.EXAM, GoalType.DAILY),
                PlanItemFixture.createPlanItem(ActivityType.STUDY, GoalType.DAILY),
                PlanItemFixture.createPlanItem(ActivityType.STUDY, GoalType.WEEKLY),
                PlanItemFixture.createPlanItem(ActivityType.PROBLEM, GoalType.WEEKLY)
        );
        List<LearningWithSolvingCountQueryDto> queryDtos = List.of(new LearningWithSolvingCountQueryDto(LearningMode.EXAM, 10L, LocalDateTime.now(), 10L));
        WeekPeriod currentWeekPeriod = new WeekPeriod(LocalDate.now().withDayOfMonth(1), LocalDate.now().plusDays(6));

        given(weeklyDefineStrategy.getWeekPeriod(LocalDate.now())).willReturn(currentWeekPeriod);
        given(learningRepository.findLearningWithSolvingCount(member, member.getCurrentCertificate(),
                LocalDate.now(), LocalDate.now())).willReturn(queryDtos);
        given(learningRepository.findLearningWithSolvingCount(member, member.getCurrentCertificate(),
                currentWeekPeriod.getStart(), currentWeekPeriod.getEnd())).willReturn(queryDtos);

        //when
        planProgressService.createCurrentPlanProgress(member, planItems);

        //then
        verify(planProgressRepository, times(2)).saveAll(any());
    }

    @Test
    @DisplayName("없는 goalType에 대해 DB 로직 호출을 하지 않는다. ")
    void callSaveAllOne() {
        //given
        List<PlanItem> planItems = Arrays.asList(
                PlanItemFixture.createPlanItem(ActivityType.EXAM, GoalType.DAILY),
                PlanItemFixture.createPlanItem(ActivityType.STUDY, GoalType.DAILY)
        );
        List<LearningWithSolvingCountQueryDto> queryDtos = List.of(new LearningWithSolvingCountQueryDto(LearningMode.EXAM, 10L, LocalDateTime.now(), 10L));

        given(weeklyDefineStrategy.getWeekPeriod(LocalDate.now())).willReturn(new WeekPeriod(LocalDate.now(), LocalDate.now().plusDays(1)));
        given(learningRepository.findLearningWithSolvingCount(member, member.getCurrentCertificate(),
                LocalDate.now(), LocalDate.now())).willReturn(queryDtos);

        //when
        planProgressService.createCurrentPlanProgress(member, planItems);

        //then
        verify(planProgressRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("calculateProgress 메소드 progress 계산 로직 테스트")
    void calculateProgressTest() {
        //given
        List<PlanProgress> planProgresses = Arrays.asList(
                createPlanProgress(LocalDate.now(), GoalType.DAILY, ActivityType.EXAM),
                createPlanProgress(LocalDate.now(), GoalType.DAILY, ActivityType.STUDY),
                createPlanProgress(LocalDate.now(), GoalType.DAILY, ActivityType.PROBLEM),
                createPlanProgress(LocalDate.now(), GoalType.DAILY, ActivityType.TIME)
        );
        List<LearningWithSolvingCountQueryDto> datas = dummyDatas();


        //when
        List<PlanProgress> result = planProgressService.calculateProgress(planProgresses, datas);

        //then
        result.forEach((planProgress -> {
            long expectedValue = -1;
            if (planProgress.getActivityType().equals(ActivityType.EXAM)) {
                expectedValue = datas.stream()
                        .filter(pi -> pi.getMode().equals(LearningMode.EXAM))
                        .count();
            }
            if (planProgress.getActivityType().equals(ActivityType.STUDY)) {
                expectedValue = datas.stream()
                        .filter(pi -> pi.getMode().equals(LearningMode.STUDY))
                        .count();
            }
            if (planProgress.getActivityType().equals(ActivityType.PROBLEM)) {
                expectedValue = datas.stream()
                        .mapToLong(LearningWithSolvingCountQueryDto::getSolvingCount).sum();
            }
            if (planProgress.getActivityType().equals(ActivityType.TIME)) {
                expectedValue = datas.stream()
                        .mapToLong(LearningWithSolvingCountQueryDto::getLearningTime).sum();
            }

            assertThat(planProgress.getCompletedValue()).isEqualTo(expectedValue);
        }));

    }


    @Test
    @DisplayName("modifyCurrentPlanProgress 메소드 PlanProgress의 값을 변경한다.")
    void modifyCurrentPlanProgressTest() {
        //given
        Plan plan = PlanFixture.createPlan(member, 1L);
        List<PlanItem> modifiedPlanItems = Arrays.asList(
                new PlanItem(plan, ActivityType.EXAM, GoalType.DAILY, 10),
                new PlanItem(plan, ActivityType.STUDY, GoalType.DAILY, 5)
        );
        List<PlanProgress> dbSavedPlanProgress = Arrays.asList(
                new PlanProgress(plan, LocalDate.now(), ActivityType.EXAM, GoalType.DAILY, 5, 0L), //수정될 대상 - 1
                new PlanProgress(plan, LocalDate.now(), ActivityType.STUDY, GoalType.DAILY, 5, 0L), //수정될 대상 - 2
                new PlanProgress(plan, LocalDate.now(), ActivityType.PROBLEM, GoalType.DAILY, 5, 0L) //수정이 되면 안됨 - 1
        );
        given(planProgressRepository.findAllByPlanAndProgressDateBetweenAndGoalType(plan, LocalDate.now(), LocalDate.now(), GoalType.DAILY)).willReturn(dbSavedPlanProgress);

        //when
        planProgressService.modifyCurrentPlanProgress(plan, modifiedPlanItems, List.of());

        //then
        assertThat(dbSavedPlanProgress.get(0).getTargetValue()).isEqualTo(modifiedPlanItems.get(0).getTargetValue());
        assertThat(dbSavedPlanProgress.get(1).getTargetValue()).isEqualTo(modifiedPlanItems.get(1).getTargetValue());
    }

    @Test
    @DisplayName("removeCurrentPlanProgress 메소드, 삭제되는 PlanProgress를 삭제한다.")
    void removePlanProgressTest(){
        //given
        Plan plan = PlanFixture.createPlan(member, 1L);
        List<PlanItem> removePlanItems = Arrays.asList(
                new PlanItem(plan, ActivityType.EXAM, GoalType.DAILY, 10),
                new PlanItem(plan, ActivityType.STUDY, GoalType.DAILY, 5)
        );
        List<PlanProgress> dbSavedPlanProgress = Arrays.asList(
                new PlanProgress(plan, LocalDate.now(), ActivityType.EXAM, GoalType.DAILY, 5, 0L), //삭제될 대상 -1
                new PlanProgress(plan, LocalDate.now(), ActivityType.STUDY, GoalType.DAILY, 5, 0L), //삭제될 대상 - 2
                new PlanProgress(plan, LocalDate.now(), ActivityType.PROBLEM, GoalType.DAILY, 5, 0L)
        );
        given(planProgressRepository.findAllByPlanAndProgressDateBetweenAndGoalType(plan, LocalDate.now(), LocalDate.now(), GoalType.DAILY)).willReturn(dbSavedPlanProgress);


        //when
        planProgressService.removeCurrentPlanProgress(plan, removePlanItems, List.of());

        //then
        verify(planProgressRepository, times(2)).delete(any());
    }


    private List<LearningWithSolvingCountQueryDto> dummyDatas() {
        List<LearningWithSolvingCountQueryDto> datas = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        datas.add(new LearningWithSolvingCountQueryDto(LearningMode.EXAM, 5000L, now, 1L));
        datas.add(new LearningWithSolvingCountQueryDto(LearningMode.EXAM, 5000L, now, 2L));
        datas.add(new LearningWithSolvingCountQueryDto(LearningMode.EXAM, 5000L, now, 3L));
        datas.add(new LearningWithSolvingCountQueryDto(LearningMode.EXAM, 5000L, now, 4L));
        datas.add(new LearningWithSolvingCountQueryDto(LearningMode.STUDY, 5000L, now, 5L));
        datas.add(new LearningWithSolvingCountQueryDto(LearningMode.STUDY, 5000L, now, 6L));
        datas.add(new LearningWithSolvingCountQueryDto(LearningMode.STUDY, 5000L, now, 7L));
        datas.add(new LearningWithSolvingCountQueryDto(LearningMode.STUDY, 5000L, now, 8L));
        datas.add(new LearningWithSolvingCountQueryDto(LearningMode.STUDY, 5000L, now, 9L));
        datas.add(new LearningWithSolvingCountQueryDto(LearningMode.EXAM, 5000L, now, 10L));
        return datas;
    }
}
