package com.jabiseo.api.learning.application.usecase;

import com.jabiseo.api.learning.dto.CreateLearningRequest;
import com.jabiseo.api.learning.dto.ProblemResultRequest;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.service.CertificateService;
import com.jabiseo.domain.learning.domain.Learning;
import com.jabiseo.domain.learning.domain.LearningMode;
import com.jabiseo.domain.learning.domain.ProblemSolving;
import com.jabiseo.domain.learning.service.LearningService;
import com.jabiseo.domain.learning.service.ProblemSolvingService;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.domain.plan.domain.Plan;
import com.jabiseo.domain.plan.dto.LearningResult;
import com.jabiseo.domain.plan.service.PlanProgressService;
import com.jabiseo.domain.plan.service.PlanService;
import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.domain.problem.exception.ProblemBusinessException;
import com.jabiseo.domain.problem.exception.ProblemErrorCode;
import com.jabiseo.domain.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateLearningUseCase {

    private final MemberService memberService;
    private final ProblemService problemService;
    private final LearningService learningService;
    private final ProblemSolvingService problemSolvingService;
    private final PlanProgressService planProgressService;
    private final CertificateService certificateService;
    private final PlanService planService;

    public Long execute(Long memberId, CreateLearningRequest request) {

        validateDuplicatedSolving(request);

        Member member = memberService.getById(memberId);
        Certificate certificate = certificateService.getById(request.certificateId());

        //문제들의 id 리스트를 뽑아내 한 번의 쿼리로 찾아옴
        List<Problem> solvedProblems = findSolvedProblems(request);

        //요청 개수와 실제 데이터 개수가 다르면 옳지 않은 문제 ID가 요청되었다는 것
        validateSolvedProblems(request, solvedProblems, certificate);

        Learning learning = Learning.of(LearningMode.valueOf(request.learningMode()), request.learningTime(), member, certificate);
        learningService.save(learning);

        //ProblemSolving 생성 및 저장
        List<ProblemSolving> problemSolvings = createProblemSolvings(request, solvedProblems, member, learning);
        problemSolvingService.saveAll(problemSolvings);

        Optional<Plan> planByMember = planService.findPlanByMember(member);
        planByMember.ifPresent(plan -> planProgressService.updateProgress(plan, LearningResult.valueOf(learning, (long) problemSolvings.size())));

        return learning.getId();
    }

    private void validateDuplicatedSolving(CreateLearningRequest request) {
        if (request.problems().stream().distinct().count() != request.problems().size()) {
            throw new ProblemBusinessException(ProblemErrorCode.DUPLICATED_SOLVING_PROBLEM);
        }
    }

    private List<Problem> findSolvedProblems(CreateLearningRequest request) {
        List<Long> solvedProblemIds = request.problems().stream()
                .map(ProblemResultRequest::problemId)
                .toList();
        return problemService.findAllByIdWithCertificate(solvedProblemIds);
    }

    private void validateSolvedProblems(CreateLearningRequest request, List<Problem> solvedProblems, Certificate certificate) {
        if (solvedProblems.size() != request.problems().size()) {
            throw new ProblemBusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND);
        }
        solvedProblems.forEach(problem -> problem.validateProblemInCertificate(certificate));
    }

    private static List<ProblemSolving> createProblemSolvings(CreateLearningRequest request, List<Problem> solvedProblems, Member member, Learning learning) {
        // solvedProblems와 request의 choice들을 매핑하기 위해 HashMap 사용
        Map<Long, Integer> problemIdToChoice = request.problems().stream()
                .collect(Collectors.toMap(
                        ProblemResultRequest::problemId,
                        ProblemResultRequest::choice
                ));
        return solvedProblems.stream()
                .map(problem -> ProblemSolving.of(
                        member,
                        problem,
                        learning,
                        problemIdToChoice.get(problem.getId()),
                        problem.checkAnswer(problemIdToChoice.get(problem.getId()))
                ))
                .toList();
    }
}
