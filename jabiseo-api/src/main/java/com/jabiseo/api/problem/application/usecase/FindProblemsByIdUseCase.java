package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.response.CertificateResponse;
import com.jabiseo.api.problem.dto.request.FindProblemsRequest;
import com.jabiseo.api.problem.dto.response.FindProblemsResponse;
import com.jabiseo.api.problem.dto.response.ProblemsDetailResponse;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.domain.problem.exception.ProblemBusinessException;
import com.jabiseo.domain.problem.exception.ProblemErrorCode;
import com.jabiseo.domain.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindProblemsByIdUseCase {

    private final MemberService memberService;
    private final ProblemService problemService;

    public FindProblemsResponse execute(Long memberId, FindProblemsRequest request) {
        Member member = memberService.getByIdWithCertificate(memberId);
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
