package com.jabiseo.api.plan.application.usecase;


import com.jabiseo.api.plan.dto.request.ModifyPlanRequest;
import com.jabiseo.api.plan.dto.request.PlanItemRequest;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.repository.CertificateRepository;
import com.jabiseo.domain.learning.domain.Learning;
import com.jabiseo.domain.learning.domain.LearningMode;
import com.jabiseo.domain.learning.domain.ProblemSolving;
import com.jabiseo.domain.learning.dto.LearningWithSolvingCountQueryDto;
import com.jabiseo.domain.learning.repository.LearningRepository;
import com.jabiseo.domain.learning.repository.ProblemSolvingRepository;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.repository.MemberRepository;
import com.jabiseo.domain.plan.domain.*;
import com.jabiseo.domain.plan.repository.PlanProgressRepository;
import com.jabiseo.domain.plan.repository.PlanRepository;
import com.jabiseo.domain.problem.domain.Problem;
import fixture.MemberFixture;
import fixture.ProblemFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("modify plan usecase 통합 테스트")
@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class ModifyPlanUseCaseUpdateProgressIntegrationTest {

    @Autowired
    private ModifyPlanUseCase modifyPlanUseCase;
    private WeeklyDefineStrategy sundayStartWeeklyStrategy = new SundayStartWeeklyStrategy();

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private LearningRepository learningRepository;

    @Autowired
    private ProblemSolvingRepository problemSolvingRepository;

    private Member testMember;
    private Plan testPlan;
    private List<PlanItem> testPlanItems;
    private Learning testLearning;
    private List<PlanItemRequest> requestMock; // exist Item List
    @Autowired
    private PlanProgressRepository planProgressRepository;

    @BeforeEach
    public void setup() {
        testMember = saveMember();
        testPlan = savePlan(testMember);
        testPlanItems = testPlan.getPlanItems();
        requestMock = new ArrayList<>();
        requestMock.addAll(testPlanItems.stream().map((current) -> {
            return new PlanItemRequest(10, current.getActivityType().name()); // 기존에 있는 값 로드
        }).toList());


        // plan Progress 생성
        savePlanProgress(testPlan);
    }

    @Test
    @DisplayName("modifyPlanUseCase 실행 시 이미 존재하는 PlanItem 에 대해 PlanProgress 값이 있다면 수정한다. (daily) ")
    void existsPlanProgressCompletedValueUpdate() {
        //given

        List<PlanItemRequest> addTenCompletedValueRequest = requestMock.stream().map((req) -> new PlanItemRequest(req.targetValue() + 10, req.activityType())).toList();
        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), addTenCompletedValueRequest, Collections.emptyList());

        //when
        modifyPlanUseCase.execute(testPlan.getId(), testMember.getId(), request);

        //then
        List<PlanProgress> currentResult = planProgressRepository.findAllByPlanAndProgressDateBetweenAndGoalType(testPlan, LocalDate.now(), LocalDate.now(), GoalType.DAILY);
        Map<ActivityType, Integer> requestMapTypeToCompletedValue = addTenCompletedValueRequest.stream()
                .collect(Collectors.toMap((req) -> ActivityType.valueOf(req.activityType()), PlanItemRequest::targetValue));

        assertThat(currentResult)
                .hasSize(requestMapTypeToCompletedValue.size())
                .allSatisfy(progress -> {
                    Integer targeted = requestMapTypeToCompletedValue.get(progress.getActivityType());
                    assertThat(targeted).isNotNull();
                    assertThat(targeted).isEqualTo(progress.getTargetValue());
                });
    }

    @Test
    @DisplayName("modifyPlanUseCase 실행 시 Item을 삭제하면 관련 Plan Progress도 삭제한다. (daily) ")
    void removePlanProgressByItems() {
        //given
        PlanItemRequest remove = requestMock.remove(0);
        ActivityType targetType = ActivityType.valueOf(remove.activityType());
        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), requestMock, Collections.emptyList());

        //when
        modifyPlanUseCase.execute(testPlan.getId(), testMember.getId(), request);

        //then
        List<PlanProgress> currentResult = planProgressRepository.findAllByPlanAndProgressDateBetweenAndGoalType(testPlan, LocalDate.now(), LocalDate.now(), GoalType.DAILY);
        assertThat(currentResult)
                .filteredOn(progress -> progress.getActivityType().equals(targetType))
                .isEmpty();
    }

    @Test
    @DisplayName("modifyPlanUseCase 실행 시 새로운 Item이면 관련 PlanProgress를 생성하고 Learning 값에 맞게 반영항다.")
    void addItemCreateProgressAndUpdateByLearningHistory() {
        //given
        requestMock.add(new PlanItemRequest(1000, ActivityType.TIME.name()));
        Learning learning = saveLearning(testMember, LearningMode.EXAM, 1);
        Learning learning_2 = saveLearning(testMember, LearningMode.EXAM, 1);
        long expectedCurrentLearningTime = learning.getLearningTime() + learning_2.getLearningTime();

        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), requestMock, Collections.emptyList());


        //when
        modifyPlanUseCase.execute(testPlan.getId(), testMember.getId(), request);


        //then
        List<PlanProgress> currentResult = planProgressRepository.findAllByPlanAndProgressDateBetweenAndGoalType(testPlan, LocalDate.now(), LocalDate.now(), GoalType.DAILY);


        assertThat(currentResult)
                .filteredOn(progress -> progress.getActivityType().equals(ActivityType.TIME))
                .isNotEmpty()
                .extracting(PlanProgress::getCompletedValue)
                .containsExactly(expectedCurrentLearningTime);
    }

    @Test
    @DisplayName("modifyPlanUseCase 실행 시 새로운 Item 이면 관련 PlanProgress 를 생성하고 Learning 값에 맞게 반영한다 . (weekly) ")
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
        expectedCompletedValue.put(ActivityType.PROBLEM, 6L);
        expectedCompletedValue.put(ActivityType.TIME, (learning1.getLearningTime() + learning2.getLearningTime() + learning3.getLearningTime()));
        expectedCompletedValue.put(ActivityType.STUDY, 1L);
        expectedCompletedValue.put(ActivityType.EXAM, 2L);


        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), requestMock, weeklyItem);

        //when
        modifyPlanUseCase.execute(testPlan.getId(), testMember.getId(), request);

        //then
        WeekPeriod weekPeriod = sundayStartWeeklyStrategy.getWeekPeriod(LocalDate.now());
        List<PlanProgress> currentResult = planProgressRepository.findAllByPlanAndProgressDateBetweenAndGoalType(testPlan, weekPeriod.getStart(), weekPeriod.getEnd(), GoalType.WEEKLY);


        List<LearningWithSolvingCountQueryDto> learningWithSolvingCount = learningRepository.findLearningWithSolvingCount(testMember, testMember.getCurrentCertificate(), weekPeriod.getStart(), weekPeriod.getEnd());

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

        problemSolvingRepository.saveAll(problemSolvings);
        return learningRepository.save(learning);
    }

    private Member saveMember() {
        Certificate saved = certificateRepository.save(Certificate.of("정보처리기사"));
        Member member = MemberFixture.createMember();
        member.updateCurrentCertificate(saved);
        return memberRepository.save(member);
    }

    private Plan savePlan(Member member) {
        List<PlanItem> planItems = new ArrayList<>();
        Plan plan = new Plan(member.getCurrentCertificate(), member, LocalDate.now());
        planItems.add(new PlanItem(plan, ActivityType.EXAM, GoalType.DAILY, 10));
        planItems.add(new PlanItem(plan, ActivityType.STUDY, GoalType.DAILY, 10));
        planItems.add(new PlanItem(plan, ActivityType.PROBLEM, GoalType.DAILY, 10));
        plan.addItems(planItems);
        return planRepository.save(plan);
    }
}
