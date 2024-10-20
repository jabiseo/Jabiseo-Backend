package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.application.usecase.CreateBookmarkUseCase;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.domain.problem.domain.Bookmark;
import com.jabiseo.domain.problem.domain.BookmarkRepository;
import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.domain.problem.domain.ProblemRepository;
import com.jabiseo.api.problem.dto.CreateBookmarkRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("북마크 생성 테스트")
@ExtendWith(MockitoExtension.class)
class CreateBookmarkUseCaseTest {

    @InjectMocks
    CreateBookmarkUseCase sut;

    @Mock
    MemberRepository memberRepository;

    @Mock
    ProblemRepository problemRepository;

    @Mock
    BookmarkRepository bookmarkRepository;

    @Test
    @DisplayName("북마크 생성을 성공한다.")
    void givenMemberIdAndProblemId_whenCreatingBookmark_thenCreateBookmark() {
        //given
        Long memberId = 1L;
        Long problemId = 2L;
        Member member = createMember(memberId);
        Problem problem = createProblem(problemId);
        given(memberRepository.getReferenceById(memberId)).willReturn(member);
        given(problemRepository.findById(problemId)).willReturn(Optional.of(problem));
        given(bookmarkRepository.existsByMemberIdAndProblemId(memberId, problemId)).willReturn(false);
        given(bookmarkRepository.save(any())).willReturn(Bookmark.of(member, problem));

        //when
        sut.execute(memberId, new CreateBookmarkRequest(problemId));

        //then
        ArgumentCaptor<Bookmark> bookmarkCaptor = ArgumentCaptor.forClass(Bookmark.class);
        verify(bookmarkRepository).save(bookmarkCaptor.capture());
        Bookmark savedBookmark = bookmarkCaptor.getValue();

        assertThat(savedBookmark).isNotNull();
        assertThat(savedBookmark.getMember().getId()).isEqualTo(memberId);
        assertThat(savedBookmark.getProblem().getId()).isEqualTo(problemId);
    }

    @Test
    @DisplayName("이미 북마크한 문제를 북마크하는 경우 예외처리한다.")
    void givenAlreadyExistedMemberIdAndProblemId_whenCreatingBookmark_thenReturnError() {
        //given
        Long memberId = 1L;
        Long problemId = 2L;
        given(bookmarkRepository.existsByMemberIdAndProblemId(memberId, problemId)).willReturn(true);


        //when & then
        assertThatThrownBy(() -> sut.execute(memberId, new CreateBookmarkRequest(problemId)))
                .isInstanceOf(ProblemBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ProblemErrorCode.BOOKMARK_ALREADY_EXISTS);

    }

    @Test
    @DisplayName("존재하지 않는 문제를 북마크하는 경우 예외처리한다.")
    void givenMemberIdAndNonExistedProblemId_whenCreatingBookmark_thenReturnError() {
        //given
        Long memberId = 1L;
        Long problemId = 2L;
        given(bookmarkRepository.existsByMemberIdAndProblemId(memberId, problemId)).willReturn(false);
        given(problemRepository.findById(problemId)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> sut.execute(memberId, new CreateBookmarkRequest(problemId)))
                .isInstanceOf(ProblemBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ProblemErrorCode.PROBLEM_NOT_FOUND);

    }

}
