package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.FindBookmarkedProblemsResponse;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.domain.problem.domain.ProblemRepository;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkSummaryQueryDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindBookmarkedProblemsUseCase {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final MemberRepository memberRepository;

    private final ProblemRepository problemRepository;

    //examId가 null일 경우 전체 시험을 대상으로 조회한다.
    public FindBookmarkedProblemsResponse execute(Long memberId, @Nullable Long examId,
                                                  List<Long> subjectIds, int page) {

        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);

        Member member = memberRepository.getReferenceById(memberId);
        member.validateCurrentCertificate();

        Certificate certificate = member.getCurrentCertificate();
        certificate.validateExamIdAndSubjectIds(examId, subjectIds);

        Page<ProblemWithBookmarkSummaryQueryDto> dtos =
                problemRepository.findBookmarkedSummaryByExamIdAndSubjectIdsInWithBookmark(memberId, examId, subjectIds, pageable);

        return FindBookmarkedProblemsResponse.of(
                dtos.getTotalElements(),
                dtos.getTotalPages(),
                dtos.getContent()
        );
    }
}
