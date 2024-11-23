package com.jabiseo.domain.problem.service;

import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.repository.MemberRepository;
import com.jabiseo.domain.member.exception.MemberBusinessException;
import com.jabiseo.domain.member.exception.MemberErrorCode;
import com.jabiseo.domain.problem.domain.Bookmark;
import com.jabiseo.domain.problem.repository.BookmarkRepository;
import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.domain.problem.repository.ProblemRepository;
import com.jabiseo.domain.problem.exception.ProblemBusinessException;
import com.jabiseo.domain.problem.exception.ProblemErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;

    public void validateBookmarkDuplication(Long memberId, Long problemId) {
        if (bookmarkRepository.existsByMemberIdAndProblemId(memberId, problemId)) {
            throw new ProblemBusinessException(ProblemErrorCode.BOOKMARK_ALREADY_EXISTS);
        }
    }

    @Transactional
    public void deleteBookmark(Long memberId, Long problemId) {
        Bookmark bookmark = bookmarkRepository.findByMemberIdAndProblemId(memberId, problemId)
                .orElseThrow(() -> new ProblemBusinessException(ProblemErrorCode.BOOKMARK_NOT_FOUND));

        bookmarkRepository.delete(bookmark);
    }

    @Transactional
    public Long createBookmark(Long memberId, Long problemId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ProblemBusinessException(ProblemErrorCode.PROBLEM_NOT_FOUND));
        Bookmark bookmark = Bookmark.of(member, problem);
        return bookmarkRepository.save(bookmark).getId();
    }
}
