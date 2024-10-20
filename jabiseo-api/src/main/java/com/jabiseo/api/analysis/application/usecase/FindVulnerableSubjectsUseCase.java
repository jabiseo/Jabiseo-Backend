package com.jabiseo.api.analysis.application.usecase;

import com.jabiseo.api.analysis.dto.FindVulnerableSubjectResponse;
import com.jabiseo.domain.analysis.service.AnalysisService;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindVulnerableSubjectsUseCase {

    private final MemberRepository memberRepository;
    private final AnalysisService analysisService;

    public List<FindVulnerableSubjectResponse> execute(Long memberId) {

        Member member = memberRepository.getReferenceById(memberId);
        member.validateCurrentCertificate();
        Certificate certificate = member.getCurrentCertificate();

        return analysisService.findVulnerableSubjects(member, certificate).stream()
                .map(FindVulnerableSubjectResponse::from)
                .toList();
    }

}
