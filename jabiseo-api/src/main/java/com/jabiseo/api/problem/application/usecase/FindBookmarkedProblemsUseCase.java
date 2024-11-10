package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.FindBookmarkedProblemsResponse;
import com.jabiseo.domain.certificate.service.CertificateService;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkSummaryQueryPageDto;
import com.jabiseo.domain.problem.service.ProblemService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindBookmarkedProblemsUseCase {

    private final MemberService memberService;
    private final ProblemService problemService;
    private final CertificateService certificateService;

    //examId가 null일 경우 전체 시험을 대상으로 조회한다.
    public FindBookmarkedProblemsResponse execute(Long memberId, @Nullable Long examId,
                                                  List<Long> subjectIds, int page) {

        Member member = memberService.getByIdWithCertificate(memberId);
        // member의 currentCertificate가 존재하는지 확인
        member.validateCurrentCertificate();
        // currentCertificate가 exam과 subjectIds를 가지고 있는지 확인
        certificateService.validateExamIdAndSubjectIds(member.getCurrentCertificate().getId(), examId, subjectIds);

        ProblemWithBookmarkSummaryQueryPageDto dto = problemService.findBookmarkedProblems(memberId, examId, subjectIds, page);

        return FindBookmarkedProblemsResponse.from(dto);
    }
}
