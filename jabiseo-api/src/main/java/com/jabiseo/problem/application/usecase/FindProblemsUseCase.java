package com.jabiseo.problem.application.usecase;

import com.jabiseo.certificate.domain.Certificate;
import com.jabiseo.certificate.domain.CertificateRepository;
import com.jabiseo.certificate.exception.CertificateBusinessException;
import com.jabiseo.certificate.exception.CertificateErrorCode;
import com.jabiseo.member.domain.Member;
import com.jabiseo.member.domain.MemberRepository;
import com.jabiseo.problem.domain.Problem;
import com.jabiseo.problem.domain.ProblemRepository;
import com.jabiseo.problem.dto.CertificateResponse;
import com.jabiseo.problem.dto.FindProblemsRequest;
import com.jabiseo.problem.dto.FindProblemsResponse;
import com.jabiseo.problem.dto.ProblemsResponse;
import com.jabiseo.problem.exception.ProblemBusinessException;
import com.jabiseo.problem.exception.ProblemErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindProblemsUseCase {

    private static final int MIN_PROBLEM_PER_SUBJECT_COUNT = 1;
    private static final int MAX_PROBLEM_PER_SUBJECT_COUNT = 20;


    private final CertificateRepository certificateRepository;
    private final ProblemRepository problemRepository;
    private final MemberRepository memberRepository;

    // TODO: 문제에 북마크 되어 있는지 표시해야 함
    public FindProblemsResponse execute(String certificateId, List<String> subjectIds, Optional<String> examId, int count) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new CertificateBusinessException(CertificateErrorCode.CERTIFICATE_NOT_FOUND));

        certificate.validateExamIdAndSubjectIds(examId, subjectIds);
        // TODO: controller에서 검증한다 해도 여기에 검증 로직이 안 들어가도 되는지 확인
        validateProblemCount(count);

        // TODO: 과목별로 문제를 가져와서 쿼리를 5번 날리는 로직에서 1번의 쿼리로 변경해야 함. 하지만 최종적으로 과목 순서가 유지되어야 함
        List<Problem> problems = subjectIds.stream()
                .map(subjectId -> {
                    if (examId.isPresent()) {
                        return problemRepository.findRandomByExamIdAndSubjectId(examId.get(), subjectId, count);
                    }
                    return problemRepository.findRandomBySubjectId(subjectId, count);
                })
                .flatMap(List::stream)
                .toList();

        CertificateResponse certificateResponse = CertificateResponse.from(certificate);
        List<ProblemsResponse> problemsResponses = problems.stream()
                .map(ProblemsResponse::from)
                .toList();

        return FindProblemsResponse.of(certificateResponse, problemsResponses);
    }

    // TODO: 문제에 북마크 되어 있는지 표시해야 함
    public FindProblemsResponse execute(String memberId, FindProblemsRequest request) {
        Member member = memberRepository.getReferenceById(memberId);
        member.validateCurrentCertificate();
        Certificate certificate = member.getCurrentCertificate();

        CertificateResponse certificateResponse = CertificateResponse.from(certificate);
        List<ProblemsResponse> problemsResponses = request.problemIds().stream()
                .map(problemId -> problemRepository.findById(problemId)
                        .orElseThrow(() -> new ProblemBusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND)))
                .peek(problem -> {
                    problem.validateProblemInCertificate(certificate);
                })
                .map(ProblemsResponse::from)
                .toList();
        return FindProblemsResponse.of(certificateResponse, problemsResponses);
    }

    private void validateProblemCount(int count) {
        // 문제의 개수가 올바른지 검사
        if (count < MIN_PROBLEM_PER_SUBJECT_COUNT || count > MAX_PROBLEM_PER_SUBJECT_COUNT) {
            throw new ProblemBusinessException(ProblemErrorCode.INVALID_PROBLEM_COUNT);
        }
    }
}
