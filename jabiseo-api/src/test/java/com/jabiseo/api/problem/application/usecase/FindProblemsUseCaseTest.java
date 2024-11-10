package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.ProblemsDetailResponse;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.service.CertificateService;
import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.api.problem.dto.FindProblemsResponse;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkDetailQueryDto;
import com.jabiseo.domain.problem.service.ProblemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static fixture.CertificateFixture.createCertificate;
import static fixture.ExamFixture.createExam;
import static fixture.ProblemFixture.createProblem;
import static fixture.ProblemWithBookmarkDetailQueryDtoFixture.createProblemWithBookmarkDetailQueryDto;
import static fixture.SubjectFixture.createSubject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("문제 세트 조회 테스트")
@ExtendWith(MockitoExtension.class)
class FindProblemsUseCaseTest {

    @InjectMocks
    FindProblemsUseCase sut;

    @Mock
    CertificateService certificateService;

    @Mock
    ProblemService problemService;

    @Test
    @DisplayName("findProblemsBySubjectId 메소드를 호출한다.")
    void findProblemsBySubjectId() {
        // given
        List<Long> subjectIds = List.of(1L, 2L);
        Long memberId = 3L;
        Long certificateId = 4L;
        Long examId = 5L;
        int count = 10;
        List<Long> problemIds = List.of(6L, 7L, 8L, 9L, 10L);

        Certificate certificate = createCertificate(certificateId);
        createExam(examId, certificate);
        subjectIds.forEach(s -> createSubject(s, certificate));
        List<Problem> problems = problemIds.stream().map(p -> createProblem(p, certificate)).toList();
        List<ProblemWithBookmarkDetailQueryDto> dtos = problems.stream().map(p -> createProblemWithBookmarkDetailQueryDto(p, true)).toList();

        given(certificateService.getByIdWithExams(certificateId)).willReturn(certificate);
        given(problemService.findProblemsByExamIdAndSubjectIds(memberId, examId, subjectIds, count)).willReturn(dtos);

        // when
        FindProblemsResponse result = sut.execute(memberId, certificateId, examId, subjectIds, count);

        // then
        assertThat(result.problems().stream().map(ProblemsDetailResponse::problemId)).containsExactlyElementsOf(problemIds);
    }

}
