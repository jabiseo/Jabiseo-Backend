package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.CreateBookmarkRequest;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.domain.problem.domain.Bookmark;
import com.jabiseo.domain.problem.domain.BookmarkRepository;
import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.domain.problem.domain.ProblemRepository;
import com.jabiseo.domain.problem.exception.ProblemBusinessException;
import com.jabiseo.domain.problem.exception.ProblemErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateBookmarkUseCase {

    private final MemberRepository memberRepository;

    private final ProblemRepository problemRepository;

    private final BookmarkRepository bookmarkRepository;

    public Long execute(Long memberId, CreateBookmarkRequest request) {
        if (bookmarkRepository.existsByMemberIdAndProblemId(memberId, request.problemId())) {
            throw new ProblemBusinessException(ProblemErrorCode.BOOKMARK_ALREADY_EXISTS);
        }

        Member member = memberRepository.getReferenceById(memberId);
        Problem problem = problemRepository.findById(request.problemId())
                .orElseThrow(() -> new ProblemBusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND));


        Bookmark bookmark = Bookmark.of(member, problem);

        return bookmarkRepository.save(bookmark).getId();
    }
}
