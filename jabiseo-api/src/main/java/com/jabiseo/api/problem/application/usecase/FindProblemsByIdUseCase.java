package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.CertificateResponse;
import com.jabiseo.api.problem.dto.FindProblemsRequest;
import com.jabiseo.api.problem.dto.FindProblemsResponse;
import com.jabiseo.api.problem.dto.ProblemsDetailResponse;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.domain.problem.exception.ProblemBusinessException;
import com.jabiseo.domain.problem.exception.ProblemErrorCode;
import com.jabiseo.domain.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindProblemsByIdUseCase {

    private final MemberRepository memberRepository;
    private final ProblemService problemService;

    public FindProblemsResponse execute(Long memberId, FindProblemsRequest request) {
        Member member = memberRepository.getReferenceById(memberId);
        member.validateCurrentCertificate();
        Certificate certificate = member.getCurrentCertificate();
        List<Long> problemIds = request.problemIds()
                .stream()
                .distinct()
                .toList();

        CertificateResponse certificateResponse = CertificateResponse.from(certificate);
        List<ProblemsDetailResponse> problemsDetailResponses = problemService.findProblemsById(memberId, problemIds).stream()
                .map(ProblemsDetailResponse::from)
                .toList();

        //요청 개수와 실제 데이터 개수가 다르면 옳지 않은 문제 ID가 요청되었다는 것
        if (problemsDetailResponses.size() != problemIds.size()) {
            throw new ProblemBusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND);
        }

        return FindProblemsResponse.of(certificateResponse, problemsDetailResponses);
    }
}
