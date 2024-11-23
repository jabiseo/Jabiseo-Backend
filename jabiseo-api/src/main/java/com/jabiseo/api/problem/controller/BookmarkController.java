package com.jabiseo.api.problem.controller;

import com.jabiseo.api.config.auth.AuthMember;
import com.jabiseo.api.config.auth.AuthenticatedMember;
import com.jabiseo.api.problem.application.usecase.CreateBookmarkUseCase;
import com.jabiseo.api.problem.application.usecase.DeleteBookmarkUseCase;
import com.jabiseo.api.problem.dto.request.CreateBookmarkRequest;
import com.jabiseo.api.problem.dto.request.DeleteBookmarkRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final CreateBookmarkUseCase createBookmarkUseCase;

    private final DeleteBookmarkUseCase deleteBookmarkUseCase;

    @PostMapping
    public ResponseEntity<Void> createBookmark(
            @RequestBody CreateBookmarkRequest request,
            @AuthenticatedMember AuthMember member
    ) {
        Long bookmarkId = createBookmarkUseCase.execute(member.getMemberId(), request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(bookmarkId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBookmark(
            @RequestBody DeleteBookmarkRequest request,
            @AuthenticatedMember AuthMember member
    ) {
        deleteBookmarkUseCase.execute(member.getMemberId(), request);
        return ResponseEntity.noContent().build();
    }
}
