package com.jabiseo.api.plan.application.usecase;

import com.jabiseo.api.plan.dto.request.ModifyPlanRequest;
import com.jabiseo.api.plan.dto.request.PlanItemRequest;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.repository.CertificateRepository;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.repository.MemberRepository;
import com.jabiseo.domain.plan.domain.ActivityType;
import com.jabiseo.domain.plan.domain.GoalType;
import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.domain.PlanItem;
import com.jabiseo.domain.plan.repository.PlanRepository;
import fixture.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("modify plan usecase 통합 테스트")
@SpringBootTest
@ActiveProfiles("test")
class ModifyPlanUseCaseIntegrationTest {

    @Autowired
    private ModifyPlanUseCase modifyPlanUseCase;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlanRepository planRepository;

    private Member testMember;
    private Plan testPlan;
    private List<PlanItem> testPlanItems;

    private List<PlanItemRequest> requestMock; // exist Item List

    @BeforeEach
    public void setup() {
        testMember = saveMember();
        testPlan = savePlan(testMember);
        testPlanItems = testPlan.getPlanItems();
        requestMock = new ArrayList<>();
        requestMock.addAll(testPlanItems.stream().map((current) -> {
            return new PlanItemRequest(10, current.getActivityType().name()); // 기존에 있는 값 로드
        }).toList());
    }

    @Test
    @DisplayName("기존 아이템에 새 PlanItem 이 추가될 시 DB에 저장된다.")
    void addNewPlanItem() {
        //given
        PlanItemRequest newItemRequest = new PlanItemRequest(10, ActivityType.TIME.name());
        requestMock.add(newItemRequest);
        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), requestMock, Collections.emptyList());

        //when
        modifyPlanUseCase.execute(testPlan.getId(), testMember.getId(), request);

        //then
        Optional<Plan> savedPlan = planRepository.findPlanWithItemsById(testPlan.getId());
        List<PlanItem> savedPlanItems = savedPlan.get().getPlanItems();

        // 1개의 아이템을 추가했기에 size +1 이 있는지 확인
        assertThat(savedPlanItems.size()).isEqualTo(testPlanItems.size() + 1);

        // 요청에 있는 ActivityType이 PlanItem에 존재하는지 확인
        List<ActivityType> savedActivityTypes = savedPlanItems.stream()
                .map(PlanItem::getActivityType)
                .toList();
        assertThat(savedActivityTypes).contains(ActivityType.TIME);
    }

    @Test
    @DisplayName("기존 아이템에서 삭제하면 삭제된 결과 값이 Item 에 반영한다.")
    void removePlanItems(){
        //given
        PlanItemRequest removedItem = requestMock.remove(0);
        ActivityType removedItemType = ActivityType.valueOf(removedItem.activityType());
        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), requestMock, Collections.emptyList());

        //when
        modifyPlanUseCase.execute(testPlan.getId(), testMember.getId(), request);

        //then
        Optional<Plan> savedPlan = planRepository.findPlanWithItemsById(testPlan.getId());
        List<PlanItem> savedPlanItems = savedPlan.get().getPlanItems();

        // 1개의 아이템을 삭제했기에 size + 1 이 있는지 확인
        assertThat(savedPlanItems.size()).isEqualTo(testPlanItems.size() - 1);

        // 요청에 있는 ActivityType이 PlanItem에 존재하지 않는지
        List<ActivityType> savedActivityTypes = savedPlanItems.stream()
                .map(PlanItem::getActivityType)
                .toList();
        assertThat(savedActivityTypes).doesNotContain(removedItemType);
    }

    @Test
    @DisplayName("기존 아이템에서 목표 값을 수정하면 수정한 결과가 반영된다 .")
    void modifyPlanItems(){
        //given
        PlanItem targetItem = testPlanItems.get(0);
        int targetValue = targetItem.getTargetValue() + 10;
        ActivityType targetType = targetItem.getActivityType();

        List<PlanItemRequest> list = requestMock.stream()
                .map((req) -> req.activityType().equals(targetType.name())
                        ? new PlanItemRequest(targetValue, targetType.name())
                        : req
                ).toList();
        ModifyPlanRequest request = new ModifyPlanRequest(LocalDate.now(), list, Collections.emptyList());


        //when
        modifyPlanUseCase.execute(testPlan.getId(), testMember.getId(), request);

        //then
        Optional<Plan> savedPlan = planRepository.findPlanWithItemsById(testPlan.getId());
        List<PlanItem> savedPlanItems = savedPlan.get().getPlanItems();

        assertThat(savedPlanItems.size()).isEqualTo(testPlanItems.size());

        // 요청에 있는 Activity Type의 값이 변경됐는지 확인
        Optional<PlanItem> first = savedPlanItems.stream()
                .filter(savedPlanItem -> savedPlanItem.getActivityType().equals(targetType))
                .findFirst();
        assertThat(first.isPresent()).isTrue();
        assertThat(first.get().getTargetValue()).isEqualTo(targetValue);
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
