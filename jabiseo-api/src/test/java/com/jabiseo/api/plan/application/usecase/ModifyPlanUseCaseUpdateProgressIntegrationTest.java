package com.jabiseo.api.plan.application.usecase;


import com.jabiseo.api.plan.dto.request.ModifyPlanRequest;
import com.jabiseo.api.plan.dto.request.PlanItemRequest;
import com.jabiseo.domain.learning.domain.Learning;
import com.jabiseo.domain.learning.domain.LearningMode;
import com.jabiseo.domain.learning.domain.ProblemSolving;
import com.jabiseo.domain.learning.repository.LearningRepository;
import com.jabiseo.domain.learning.repository.ProblemSolvingRepository;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.repository.MemberRepository;
import com.jabiseo.domain.plan.domain.*;
import com.jabiseo.domain.plan.repository.PlanProgressRepository;
import com.jabiseo.domain.plan.repository.PlanRepository;
import com.jabiseo.domain.problem.domain.Problem;
import fixture.ProblemFixture;
import org.junit.jupiter.api.AfterEach;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DisplayName("modify plan usecase, Update Progress 통합 테스트")
@SpringBootTest
@SqlGroup({
        @Sql(value = "/sql/modify-plan-usecase-test-data.sql", executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = AFTER_TEST_METHOD)
})
@ActiveProfiles("test")
public class ModifyPlanUseCaseUpdateProgressIntegrationTest {

    @Autowired
    private ModifyPlanUseCase modifyPlanUseCase;
    private WeeklyDefineStrategy sundayStartWeeklyStrategy = new SundayStartWeeklyStrategy();


    @Autowired
    private LearningRepository learningRepository;

    @Autowired
    private ProblemSolvingRepository problemSolvingRepository;

    @Autowired
    private PlanProgressRepository planProgressRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private MemberRepository memberRepository;


    List<PlanItemRequest> existPlanItems = new ArrayList<>();
    Plan testPlan;
    Member testMember;

    @BeforeEach
    public void setup() {
        // 시간과 관련된 Learning, plan Progress는 코드로 생성
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
        testPlan = planRepository.findPlanWithItemsById(1L).orElse(null);
        testMember = memberRepository.findById(1L).orElse(null);
        savePlanProgress(testPlan); // given Plan Progress

    }

    @AfterEach
    public void tearDown() {
        planProgressRepository.deleteAll();
        learningRepository.deleteAll();
    }

    @Test
    @DisplayName("이미 존재하는 PlanItem 에 대해 PlanProgress 값이 있다면 수정한다. (daily) ")
    void existsPlanProgressCompletedValueUpdate() {
        //given
        List<PlanItemRequest> addTenCompletedValueRequest = existPlanItems.stream().map((req) -> new PlanItemRequest(req.targetValue() + 10, req.activityType())).toList();
        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), addTenCompletedValueRequest, Collections.emptyList());


        //when
        modifyPlanUseCase.execute(1L, 1L, request);

        //then
        List<PlanProgress> result = planProgressRepository.findAllByPlanAndProgressDateBetweenAndGoalType(testPlan, LocalDate.now(), LocalDate.now(), GoalType.DAILY);
        Map<ActivityType, Integer> requestMapTypeToCompletedValue = addTenCompletedValueRequest.stream()
                .collect(Collectors.toMap((req) -> ActivityType.valueOf(req.activityType()), PlanItemRequest::targetValue));

        assertThat(result)
                .hasSize(requestMapTypeToCompletedValue.size())
                .allSatisfy(progress -> {
                    Integer targeted = requestMapTypeToCompletedValue.get(progress.getActivityType());
                    assertThat(targeted).isNotNull();
                    assertThat(targeted).isEqualTo(progress.getTargetValue());
                });
    }

    @Test
    @DisplayName(" Item을 삭제하면 관련 Plan Progress 도 삭제한다. (daily) ")
    void removePlanProgressByItems() {
        //given
        PlanItemRequest remove = existPlanItems.remove(0);
        ActivityType targetType = ActivityType.valueOf(remove.activityType());
        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), existPlanItems, Collections.emptyList());

        //when
        modifyPlanUseCase.execute(1L, 1L, request);

        //then
        List<PlanProgress> currentResult = planProgressRepository.findAllByPlanAndProgressDateBetweenAndGoalType(testPlan, LocalDate.now(), LocalDate.now(), GoalType.DAILY);
        assertThat(currentResult)
                .filteredOn(progress -> progress.getActivityType().equals(targetType))
                .isEmpty();
    }


    @Test
    @DisplayName("새로운 Item이 추가되면 관련 PlanProgress 를 생성하고 Learning 값에 맞게 반영항다.")
    void addItemCreateProgressAndUpdateByLearningHistory() {
        //given
        existPlanItems.add(new PlanItemRequest(1000, ActivityType.PROBLEM.name()));
        Learning learning = saveLearning(testMember, LearningMode.EXAM, 1);
        Learning learning_2 = saveLearning(testMember, LearningMode.EXAM, 1);
        long expectedValue = learning.getProblemSolvings().size() + learning_2.getProblemSolvings().size();

        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), existPlanItems, Collections.emptyList());


        //when
        modifyPlanUseCase.execute(1L, 1L, request);


        //then
        List<PlanProgress> currentResult = planProgressRepository.findAllByPlanAndProgressDateBetweenAndGoalType(testPlan, LocalDate.now(), LocalDate.now(), GoalType.DAILY);


        assertThat(currentResult)
                .filteredOn(progress -> progress.getActivityType().equals(ActivityType.PROBLEM))
                .isNotEmpty()
                .extracting(PlanProgress::getCompletedValue)
                .containsExactly(expectedValue);
    }

    @Test
    @DisplayName("새로운 Item 이면 관련 PlanProgress 를 생성하고 Learning 값에 맞게 반영한다 . (weekly) ")
    void addItemCreateProgressAndUpdateByLearningHistoryPerActivityType() {
        //given
        List<PlanItemRequest> weeklyItem = new ArrayList<>();
        weeklyItem.add(new PlanItemRequest(50, ActivityType.PROBLEM.name()));
        weeklyItem.add(new PlanItemRequest(50, ActivityType.STUDY.name()));
        weeklyItem.add(new PlanItemRequest(50, ActivityType.EXAM.name()));
        weeklyItem.add(new PlanItemRequest(50, ActivityType.TIME.name()));
        Learning learning1 = saveLearning(testMember, LearningMode.EXAM, 2);
        Learning learning2 = saveLearning(testMember, LearningMode.EXAM, 2);
        Learning learning3 = saveLearning(testMember, LearningMode.STUDY, 2);

        Map<ActivityType, Long> expectedCompletedValue = new HashMap<>();
        expectedCompletedValue.put(ActivityType.PROBLEM, (long) (learning1.getProblemSolvings().size() + learning2.getProblemSolvings().size() + learning3.getProblemSolvings().size()));
        expectedCompletedValue.put(ActivityType.TIME, (learning1.getLearningTime() + learning2.getLearningTime() + learning3.getLearningTime()));
        expectedCompletedValue.put(ActivityType.STUDY, 1L);
        expectedCompletedValue.put(ActivityType.EXAM, 2L);


        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), existPlanItems, weeklyItem);

        //when
        modifyPlanUseCase.execute(testPlan.getId(), testMember.getId(), request);

        //then
        WeekPeriod weekPeriod = sundayStartWeeklyStrategy.getWeekPeriod(LocalDate.now());
        List<PlanProgress> currentResult = planProgressRepository.findAllByPlanAndProgressDateBetweenAndGoalType(testPlan, weekPeriod.getStart(), weekPeriod.getEnd(), GoalType.WEEKLY);

        assertThat(currentResult.size()).isEqualTo(weeklyItem.size());
        assertThat(currentResult)
                .allSatisfy(progress -> {
                    assertThat(progress.getCompletedValue()).isEqualTo(expectedCompletedValue.get(progress.getActivityType()));
                });
    }


    private void savePlanProgress(Plan plan) {
        List<PlanItem> planItems = plan.getPlanItems();

        List<PlanProgress> planProgresses = new ArrayList<>();
        for (PlanItem planItem : planItems) {
            planProgresses.add(planItem.toPlanProgress(LocalDate.now()));
        }
        planProgressRepository.saveAll(planProgresses);
    }

    // N 개의 ProblemSolving
    private Learning saveLearning(Member member, LearningMode learningMode, int problemSolvingCount) {
        Learning learning = Learning.of(learningMode, 100L, member, member.getCurrentCertificate());
        Problem problem = ProblemFixture.createProblem((long) 1L);
        List<ProblemSolving> problemSolvings = new ArrayList<>();
        for (int i = 0; i < problemSolvingCount; i++) {
            problemSolvings.add(ProblemSolving.of(member, problem, learning, 1, true));
        }
        learning = learningRepository.save(learning);
        problemSolvingRepository.saveAll(problemSolvings);
        return learning;
    }

}
