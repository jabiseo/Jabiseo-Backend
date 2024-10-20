package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.application.usecase.DeleteBookmarkUseCase;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.problem.domain.Bookmark;
import com.jabiseo.domain.problem.domain.BookmarkRepository;
import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.api.problem.dto.DeleteBookmarkRequest;
import com.jabiseo.domain.problem.exception.ProblemBusinessException;
import com.jabiseo.domain.problem.exception.ProblemErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static fixture.MemberFixture.createMember;
import static fixture.ProblemFixture.createProblem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("북마크 삭제 테스트")
@ExtendWith(MockitoExtension.class)
class DeleteBookmarkUseCaseTest {

    @InjectMocks
    DeleteBookmarkUseCase sut;

    @Mock
    BookmarkRepository bookmarkRepository;

    @Test
    @DisplayName("북마크 삭제를 성공한다.")
    void givenMemberIdAndProblemId_whenDeletingBookmark_thenDeleteBookmark() {
        //given
        Long memberId = 1L;
        Long problemId = 2L;
        Member member = createMember(memberId);
        Problem problem = createProblem(problemId);
        Bookmark bookmark = Bookmark.of(member, problem);
        given(bookmarkRepository.findByMemberIdAndProblemId(memberId, problemId)).willReturn(Optional.of(bookmark));

        //when
        sut.execute(memberId, new DeleteBookmarkRequest(problemId));

        //then
        ArgumentCaptor<Bookmark> bookmarkCaptor = ArgumentCaptor.forClass(Bookmark.class);
        verify(bookmarkRepository).delete(bookmarkCaptor.capture());
        Bookmark deletedBookmark = bookmarkCaptor.getValue();
        assertThat(deletedBookmark).isEqualTo(bookmark);
    }

    @Test
    @DisplayName("존재하지 않는 북마크를 삭제하는 경우 예외처리한다.")
    void givenNonExistBookmarkWithMemberIdAndProblemId_whenDeletingBookmark_thenReturnError() {
        //given
        Long memberId = 1L;
        Long problemId = 2L;
        given(bookmarkRepository.findByMemberIdAndProblemId(memberId, problemId)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> sut.execute(memberId, new DeleteBookmarkRequest(problemId)))
                .isInstanceOf(ProblemBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ProblemErrorCode.BOOKMARK_NOT_FOUND);
    }

}
