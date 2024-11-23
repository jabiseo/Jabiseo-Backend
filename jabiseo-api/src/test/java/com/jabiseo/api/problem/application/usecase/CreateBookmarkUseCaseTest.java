package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.request.CreateBookmarkRequest;
import com.jabiseo.domain.problem.service.BookmarkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@DisplayName("북마크 생성 테스트")
@ExtendWith(MockitoExtension.class)
class CreateBookmarkUseCaseTest {

    @InjectMocks
    CreateBookmarkUseCase sut;

    @Mock
    BookmarkService bookmarkService;

    @Test
    @DisplayName("북마크 생성을 성공한다.")
    void givenMemberIdAndProblemId_whenCreatingBookmark_thenCreateBookmark() {
        //given
        Long memberId = 1L;
        Long problemId = 2L;

        doNothing().when(bookmarkService).validateBookmarkDuplication(memberId, problemId);

        //when
        sut.execute(memberId, new CreateBookmarkRequest(problemId));

        //then
        verify(bookmarkService).createBookmark(memberId, problemId);
    }

}
