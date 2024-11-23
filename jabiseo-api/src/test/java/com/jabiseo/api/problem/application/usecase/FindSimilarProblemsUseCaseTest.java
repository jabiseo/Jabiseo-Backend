package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.api.problem.dto.response.FindSimilarProblemResponse;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkSummaryQueryDto;
import com.jabiseo.domain.problem.service.ProblemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static fixture.CertificateFixture.createCertificate;
import static fixture.ProblemFixture.createProblem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("유사 문제 조회 테스트")
@ExtendWith(MockitoExtension.class)
class FindSimilarProblemsUseCaseTest {

    @InjectMocks
    FindSimilarProblemsUseCase sut;

    @Mock
    ProblemService problemService;

    @Test
    @DisplayName("유사 문제 조회를 성공한다.")
    void givenMemberIdAndProblemId_whenFindingSimilarProblems_thenFindSimilarProblems() {
        //given
        Long memberId = 1L;
        Long problemId = 2L;
        Long examId = 3L;
        Long subjectId = 4L;
        Long certificateId = 5L;
        List<Long> similarProblemIds = List.of(5L, 6L, 7L);

        Certificate certificate = createCertificate(certificateId);
        Problem problem = createProblem(problemId, certificate);
        List<ProblemWithBookmarkSummaryQueryDto> problemWithBookmarkSummaryQueryDtos =
                similarProblemIds.stream()
                        .map(id -> new ProblemWithBookmarkSummaryQueryDto(
                                id, "문제", false, examId, "설명", subjectId, "이름", 1
                        ))
                        .toList();

        given(problemService.getById(problemId)).willReturn(problem);
        given(problemService.findSimilarProblems(memberId, problemId, certificateId)).willReturn(problemWithBookmarkSummaryQueryDtos);

        //when
        List<FindSimilarProblemResponse> result = sut.execute(memberId, problemId);

        //then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).problemId()).isEqualTo(5L);
        assertThat(result.get(1).problemId()).isEqualTo(6L);
        assertThat(result.get(2).problemId()).isEqualTo(7L);
    }

}
