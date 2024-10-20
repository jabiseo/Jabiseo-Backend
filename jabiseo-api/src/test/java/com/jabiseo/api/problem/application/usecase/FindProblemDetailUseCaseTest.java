package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.application.usecase.FindProblemDetailUseCase;
import com.jabiseo.domain.problem.domain.ProblemRepository;
import com.jabiseo.api.problem.dto.FindProblemDetailResponse;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkDetailQueryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("문제 상세 조회 테스트")
@ExtendWith(MockitoExtension.class)
class FindProblemDetailUseCaseTest {

    @InjectMocks
    FindProblemDetailUseCase sut;

    @Mock
    ProblemRepository problemRepository;

    @Test
    @DisplayName("문제 상세 조회를 성공한다.")
    void givenMemberIdAndProblemId_whenFindingProblemDetail_thenFindProblemDetail() throws Exception {
        //given
        Long memberId = 1L;
        Long problemId = 2L;
        Long examId = 3L;
        Long subjectId = 4L;
        ProblemWithBookmarkDetailQueryDto problemWithBookmarkDetailQueryDto = new ProblemWithBookmarkDetailQueryDto(
            problemId, "문제", "선지1", "선지2", "선지3", "선지4", 1, "해설", false, examId, "설명", subjectId, "이름", 1
        );

        given(problemRepository.findDetailByIdWithBookmark(memberId, problemId)).willReturn(problemWithBookmarkDetailQueryDto);

        //when
        FindProblemDetailResponse result = sut.execute(memberId, problemId);

        //then
        assertThat(result.problemId()).isEqualTo(problemId);
        assertThat(result.examInfo().examId()).isEqualTo(examId);
        assertThat(result.subjectInfo().subjectId()).isEqualTo(subjectId);
    }

}
