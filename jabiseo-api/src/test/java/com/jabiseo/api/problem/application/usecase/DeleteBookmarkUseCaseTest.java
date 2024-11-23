package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.request.DeleteBookmarkRequest;
import com.jabiseo.domain.problem.service.BookmarkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@DisplayName("북마크 삭제 테스트")
@ExtendWith(MockitoExtension.class)
class DeleteBookmarkUseCaseTest {

    @InjectMocks
    DeleteBookmarkUseCase sut;

    @Mock
    BookmarkService bookmarkService;

    @Test
    @DisplayName("북마크 삭제를 성공한다.")
    void givenMemberIdAndProblemId_whenDeletingBookmark_thenDeleteBookmark() {
        //given
        Long memberId = 1L;
        Long problemId = 2L;

        //when
        sut.execute(memberId, new DeleteBookmarkRequest(problemId));

        //then
        verify(bookmarkService).deleteBookmark(memberId, problemId);
    }

}
