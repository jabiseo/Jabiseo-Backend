package com.jabiseo.api.learning.application.usecase;

import com.jabiseo.api.learning.dto.CreateLearningRequest;
import com.jabiseo.api.learning.dto.ProblemResultRequest;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.exception.CertificateBusinessException;
import com.jabiseo.domain.certificate.exception.CertificateErrorCode;
import com.jabiseo.domain.certificate.service.CertificateService;
import com.jabiseo.domain.learning.domain.Learning;
import com.jabiseo.domain.learning.domain.ProblemSolving;
import com.jabiseo.domain.learning.service.LearningService;
import com.jabiseo.domain.learning.service.ProblemSolvingService;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.service.PlanProgressService;
import com.jabiseo.domain.plan.service.PlanService;
import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.domain.problem.exception.ProblemBusinessException;
import com.jabiseo.domain.problem.exception.ProblemErrorCode;
import com.jabiseo.domain.problem.service.ProblemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static fixture.CertificateFixture.createCertificate;
import static fixture.MemberFixture.createMember;
import static fixture.ProblemFixture.createProblem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("결과 제출 (Learning 생성) 테스트")
class CreateLearningUseCaseTest {

    @InjectMocks
    CreateLearningUseCase sut;

    @Mock
    MemberService memberService;

    @Mock
    CertificateService certificateService;

    @Mock
    ProblemService problemService;

    @Mock
    LearningService learningService;

    @Mock
    ProblemSolvingService problemSolvingService;

    @Mock
    PlanProgressService planProgressService;

    @Mock
    PlanService planService;

    @Test
    @DisplayName("한 문제를 여러 번 풀었다는 결과가 주어지면 예외가 발생한다.")
    void givenDuplicatedProblem_whenCreateLearning_thenThrowsException() {
        //given
        Long learningTime = 100L;
        Long memberId = 1L;
        Long certificateId = 2L;
        List<Long> problemIds = List.of(3L, 3L);

        Certificate certificate = createCertificate(certificateId);
        List<Problem> problems = problemIds.stream().map(problemId -> createProblem(problemId, certificate)).toList();

        CreateLearningRequest request = new CreateLearningRequest(learningTime, "EXAM", certificateId,
                problems.stream().map(problem -> new ProblemResultRequest(problem.getId(), 1)).toList());

        //when
        assertThatThrownBy(() -> sut.execute(memberId, request))
                .isInstanceOf(ProblemBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ProblemErrorCode.DUPLICATED_SOLVING_PROBLEM);
    }

    @Test
    @DisplayName("존재하지 않는 문제로 학습 결과 제출 시 예외가 발생한다.")
    void givenNonExistedProblem_whenCreateLearning_thenThrowsException() {
        //given
        Long learningTime = 100L;
        Long memberId = 1L;
        Long certificateId = 2L;
        List<Long> problemIds = List.of(3L, 4L);
        Member member = createMember(memberId);
        Certificate certificate = createCertificate(certificateId);
        List<Problem> problems = problemIds.stream().map(problemId -> createProblem(problemId, certificate)).toList();

        given(memberService.getById(memberId)).willReturn(member);
        given(certificateService.getById(certificateId)).willReturn(certificate);
        given(problemService.findAllByIdWithCertificate(problemIds)).willReturn(List.of(problems.get(0)));

        CreateLearningRequest request = new CreateLearningRequest(learningTime, "EXAM", certificateId,
                problems.stream().map(problem -> new ProblemResultRequest(problem.getId(), 1)).toList());

        //when & then
        assertThatThrownBy(() -> sut.execute(memberId, request))
                .isInstanceOf(ProblemBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ProblemErrorCode.PROBLEM_NOT_FOUND);
    }


    @Test
    @DisplayName("자격증에 속하지 않는 문제로 학습 결과 제출 시 예외가 발생한다.")
    void givenNotMatchedProblemId_whenCreateLearning_thenThrowsException() {
        //given
        Long learningTime = 100L;
        Long memberId = 1L;
        Long certificateId = 2L;
        Long anotherCertificateId = 100L;
        List<Long> problemIds = List.of(3L, 4L);
        Member member = createMember(memberId);
        Certificate certificate = createCertificate(certificateId);
        Certificate anotherCertificate = createCertificate(anotherCertificateId);
        List<Problem> problems = problemIds.stream().map(problemId -> createProblem(problemId, anotherCertificate)).toList();

        given(memberService.getById(memberId)).willReturn(member);
        given(certificateService.getById(certificateId)).willReturn(certificate);
        given(problemService.findAllByIdWithCertificate(problemIds)).willReturn(problems);

        CreateLearningRequest request = new CreateLearningRequest(learningTime, "EXAM", certificateId,
                problems.stream().map(problem -> new ProblemResultRequest(problem.getId(), 1)).toList());

        //when & then
        assertThatThrownBy(() -> sut.execute(memberId, request))
                .isInstanceOf(CertificateBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", CertificateErrorCode.PROBLEM_NOT_FOUND_IN_CERTIFICATE);
    }

    @Test
    @DisplayName("문제 풀이 결과가 주어지면 학습 결과 생성에 성공한다.")
    void givenResultOfProblemSolving_whenCreateLearning_thenCreateLearning() {
        //given
        Long learningTime = 100L;
        Long memberId = 1L;
        Long certificateId = 2L;
        List<Long> problemIds = List.of(3L, 4L);
        Member member = createMember(memberId);
        Certificate certificate = createCertificate(certificateId);
        List<Problem> problems = problemIds.stream().map(problemId -> createProblem(problemId, certificate)).toList();

        given(memberService.getById(memberId)).willReturn(member);
        given(certificateService.getById(certificateId)).willReturn(certificate);
        given(problemService.findAllByIdWithCertificate(problemIds)).willReturn(problems);
        given(planService.findPlanByMember(member)).willReturn(Optional.of(new Plan(certificate, member, LocalDate.now())));

        CreateLearningRequest request = new CreateLearningRequest(learningTime, "EXAM", certificateId,
                problems.stream().map(problem -> new ProblemResultRequest(problem.getId(), 1)).toList());

        //when
        sut.execute(memberId, request);

        //then
        ArgumentCaptor<Learning> learningCaptor = ArgumentCaptor.forClass(Learning.class);
        ArgumentCaptor<List<ProblemSolving>> problemSolvingCaptor = ArgumentCaptor.forClass(List.class);

        verify(learningService).save(learningCaptor.capture());
        verify(problemSolvingService).saveAll(problemSolvingCaptor.capture());
        verify(planProgressService).updateProgress(any(), any());

        List<ProblemSolving> capturedProblemSolvings = problemSolvingCaptor.getValue();
        assertThat(capturedProblemSolvings.stream().map(ps -> ps.getProblem().getId()))
                .containsExactlyInAnyOrder(3L, 4L);
    }

}
