package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.response.CertificateResponse;
import com.jabiseo.api.problem.dto.response.FindProblemsResponse;
import com.jabiseo.api.problem.dto.response.ProblemsDetailResponse;
import com.jabiseo.domain.analysis.service.AnalysisService;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.domain.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindRecommendedProblemsUseCase {

    private final MemberService memberService;
    private final AnalysisService analysisService;
    private final ProblemService problemService;

    public FindProblemsResponse execute(Long memberId) {
        Member member = memberService.getByIdWithCertificate(memberId);
        member.validateCurrentCertificate();
        Certificate certificate = member.getCurrentCertificate();

        List<Long> vulnerableProblems = analysisService.findVulnerableProblems(member, certificate);

        CertificateResponse certificateResponse = CertificateResponse.from(certificate);
        List<ProblemsDetailResponse> problemsDetailResponses = problemService.findProblemsById(memberId, vulnerableProblems).stream()
                .map(ProblemsDetailResponse::from)
                .toList();

        return FindProblemsResponse.of(certificateResponse, problemsDetailResponses);
    }

}
