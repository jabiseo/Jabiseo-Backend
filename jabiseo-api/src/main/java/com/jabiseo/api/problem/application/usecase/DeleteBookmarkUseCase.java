package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.DeleteBookmarkRequest;
import com.jabiseo.domain.problem.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteBookmarkUseCase {

    private final BookmarkService bookmarkService;

    public void execute(Long memberId, DeleteBookmarkRequest request) {
        bookmarkService.deleteBookmark(memberId, request.problemId());
    }
}
