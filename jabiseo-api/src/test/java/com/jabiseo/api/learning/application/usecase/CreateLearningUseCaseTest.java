package com.jabiseo.api.learning.application.usecase;

import com.jabiseo.api.learning.application.usecase.CreateLearningUseCase;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.domain.CertificateRepository;
import com.jabiseo.domain.certificate.exception.CertificateBusinessException;
import com.jabiseo.domain.certificate.exception.CertificateErrorCode;
import com.jabiseo.domain.learning.domain.Learning;
import com.jabiseo.domain.learning.domain.LearningRepository;
import com.jabiseo.domain.learning.domain.ProblemSolving;
import com.jabiseo.domain.learning.domain.ProblemSolvingRepository;
import com.jabiseo.api.learning.dto.CreateLearningRequest;
import com.jabiseo.api.learning.dto.ProblemResultRequest;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.domain.plan.domain.PlanProgressService;
import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.domain.problem.domain.ProblemRepository;
import com.jabiseo.domain.problem.exception.ProblemBusinessException;
import com.jabiseo.domain.problem.exception.ProblemErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static fixture.CertificateFixture.createCertificate;
import static fixture.LearningFixture.createLearning;
import static fixture.MemberFixture.createMember;
import static fixture.ProblemFixture.createProblem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("결과 제출 (Learning 생성) 테스트")
class CreateLearningUseCaseTest {

    @InjectMocks
    CreateLearningUseCase sut;

    @Mock
    MemberRepository memberRepository;

    @Mock
    CertificateRepository certificateRepository;

    @Mock
    ProblemRepository problemRepository;

    @Mock
    LearningRepository learningRepository;

    @Mock
    ProblemSolvingRepository problemSolvingRepository;

    @Mock
    PlanProgressService planProgressService;

    @Test
    @DisplayName("존재하지 않는 자격증으로 학습 결과 제출 시 예외가 발생한다.")
    void givenNonExistedCertificate_whenCreateLearning_thenThrowsException() {
        //given
        Long learningTime = 100L;
        Long memberId = 1L;
        Long certificateId = 2L;
        Member member = createMember(memberId);
        given(memberRepository.getReferenceById(memberId)).willReturn(member);
        CreateLearningRequest request = new CreateLearningRequest(learningTime, "EXAM", certificateId, List.of());

        //when & then
        assertThatThrownBy(() -> sut.execute(memberId, request))
                .isInstanceOf(CertificateBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", CertificateErrorCode.CERTIFICATE_NOT_FOUND);
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
        given(memberRepository.getReferenceById(memberId)).willReturn(member);
        given(certificateRepository.findById(certificateId)).willReturn(Optional.of(certificate));
        given(problemRepository.findAllById(problemIds)).willReturn(List.of());

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
        Long anotherCertificateId = 10L;
        List<Long> problemIds = List.of(3L, 4L);

        Member member = createMember(memberId);
        Certificate certificate = createCertificate(anotherCertificateId);
        Certificate anotherCertificate = createCertificate(anotherCertificateId);
        List<Problem> problems = problemIds.stream().map(problemId -> createProblem(problemId, anotherCertificate)).toList();

        given(memberRepository.getReferenceById(memberId)).willReturn(member);
        given(certificateRepository.findById(certificateId)).willReturn(Optional.of(certificate));
        given(problemRepository.findAllById(problemIds)).willReturn(problems);

        CreateLearningRequest request = new CreateLearningRequest(learningTime, "EXAM", certificateId,
                problems.stream().map(problem -> new ProblemResultRequest(problem.getId(), 1)).toList());

        //when & then
        assertThatThrownBy(() -> sut.execute(memberId, request))
                .isInstanceOf(CertificateBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", CertificateErrorCode.PROBLEM_NOT_FOUND_IN_CERTIFICATE);
    }

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

        given(certificateRepository.findById(certificateId)).willReturn(Optional.of(certificate));

        CreateLearningRequest request = new CreateLearningRequest(learningTime, "EXAM", certificateId,
                problems.stream().map(problem -> new ProblemResultRequest(problem.getId(), 1)).toList());

        //when
        assertThatThrownBy(() -> sut.execute(memberId, request))
                .isInstanceOf(ProblemBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ProblemErrorCode.DUPLICATED_SOLVING_PROBLEM);
    }

    @Test
    @DisplayName("문제 풀이 결과가 주어지면 학습 결과 생성에 성공한다.")
    void givenResultOfProblemSolving_whenCreateLearning_thenCreateLearning() {
        //given
        Long learningTime = 100L;
        Long memberId = 1L;
        Long certificateId = 2L;
        List<Long> problemIds = List.of(3L, 4L);
        Long learningId = 5L;

        Member member = createMember(memberId);
        Certificate certificate = createCertificate(certificateId);
        List<Problem> problems = problemIds.stream().map(problemId -> createProblem(problemId, certificate)).toList();
        Learning learning = createLearning(learningId, member, certificate);

        given(memberRepository.getReferenceById(memberId)).willReturn(member);
        given(certificateRepository.findById(certificateId)).willReturn(Optional.of(certificate));
        given(problemRepository.findAllById(problemIds)).willReturn(problems);
        given(problemSolvingRepository.saveAll(any())).willReturn(List.of());
        given(learningRepository.save(any())).willReturn(learning);

        CreateLearningRequest request = new CreateLearningRequest(learningTime, "EXAM", certificateId,
                problems.stream().map(problem -> new ProblemResultRequest(problem.getId(), 1)).toList());

        //when
        sut.execute(memberId, request);

        //then
        ArgumentCaptor<Learning> learningCaptor = ArgumentCaptor.forClass(Learning.class);
        verify(learningRepository).save(learningCaptor.capture());
        Learning savedLearning = learningCaptor.getValue();
        assertThat(savedLearning).isNotNull();

        ArgumentCaptor<List<ProblemSolving>> problemSolvingCaptor = ArgumentCaptor.forClass(List.class);
        verify(problemSolvingRepository).saveAll(problemSolvingCaptor.capture());
        List<ProblemSolving> savedProblemSolvings = problemSolvingCaptor.getValue();
        assertThat(savedProblemSolvings).isNotEmpty();
    }

}
