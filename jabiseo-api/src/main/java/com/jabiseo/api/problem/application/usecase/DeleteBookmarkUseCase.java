package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.DeleteBookmarkRequest;
import com.jabiseo.domain.problem.domain.Bookmark;
import com.jabiseo.domain.problem.domain.BookmarkRepository;
import com.jabiseo.domain.problem.exception.ProblemBusinessException;
import com.jabiseo.domain.problem.exception.ProblemErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteBookmarkUseCase {

    private final BookmarkRepository bookmarkRepository;

    public void execute(Long memberId, DeleteBookmarkRequest request) {
        Bookmark bookmark = bookmarkRepository.findByMemberIdAndProblemId(memberId, request.problemId())
                .orElseThrow(() -> new ProblemBusinessException(ProblemErrorCode.BOOKMARK_NOT_FOUND));

        bookmarkRepository.delete(bookmark);
    }
}
