package com.jabiseo.api.analysis.application.usecase;

import com.jabiseo.api.analysis.dto.FindVulnerableTagResponse;
import com.jabiseo.domain.analysis.service.AnalysisService;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindVulnerableTagsUseCase {

    private final MemberService memberService;
    private final AnalysisService analysisService;

    public List<FindVulnerableTagResponse> execute(Long memberId) {

        Member member = memberService.getByIdWithCertificate(memberId);
        member.validateCurrentCertificate();
        Certificate certificate = member.getCurrentCertificate();

        return analysisService.findVulnerableTags(member, certificate).stream()
                .map(FindVulnerableTagResponse::from)
                .toList();
    }

}
