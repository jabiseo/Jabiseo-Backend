package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.CreateBookmarkRequest;
import com.jabiseo.domain.problem.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateBookmarkUseCase {

    private final BookmarkService bookmarkService;

    public Long execute(Long memberId, CreateBookmarkRequest request) {
        bookmarkService.validateBookmarkDuplication(memberId, request.problemId());
        return bookmarkService.createBookmark(memberId, request.problemId());
    }
}
