package com.jabiseo.problem.application.usecase;

import com.jabiseo.certificate.domain.Certificate;
import com.jabiseo.member.domain.Member;
import com.jabiseo.member.domain.MemberRepository;
import com.jabiseo.problem.domain.Problem;
import com.jabiseo.problem.domain.ProblemRepository;
import com.jabiseo.problem.dto.FindBookmarkedProblemsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindBookmarkedProblemsUseCase {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final MemberRepository memberRepository;

    private final ProblemRepository problemRepository;

    public List<FindBookmarkedProblemsResponse> execute(String memberId, Optional<String> examId, List<String> subjectIds, int page) {

        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);

        Member member = memberRepository.getReferenceById(memberId);
        member.validateCurrentCertificate();

        Certificate certificate = member.getCertificateState();
        certificate.validateExamIdAndSubjectIds(examId, subjectIds);

        List<Problem> problems = examId.map(id ->
                        problemRepository.findBookmarkedByExamIdAndSubjectIdIn(memberId, id, subjectIds, pageable))
                .orElseGet(() -> problemRepository.findBookmarkedBySubjectIdIn(memberId, subjectIds, pageable));

        return problems.stream()
                .map(FindBookmarkedProblemsResponse::from)
                .toList();
    }
}