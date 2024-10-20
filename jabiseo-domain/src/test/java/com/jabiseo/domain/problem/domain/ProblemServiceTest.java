package com.jabiseo.domain.problem.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.domain.Exam;
import com.jabiseo.domain.certificate.domain.Subject;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkDetailQueryDto;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkSummaryScoreQueryDto;
import com.jabiseo.domain.problem.service.ProblemSearchProvider;
import com.jabiseo.domain.problem.service.ProblemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static fixture.CertificateFixture.createCertificate;
import static fixture.ExamFixture.createExam;
import static fixture.ProblemFixture.createProblem;
import static fixture.ProblemWithBookmarkDetailQueryDtoFixture.createProblemWithBookmarkDetailQueryDto;
import static fixture.SubjectFixture.createSubject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("문제 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class ProblemServiceTest {

    @InjectMocks
    ProblemService sut;

    @Mock
    ProblemRepository problemRepository;

    @Mock
    ProblemSearchProvider problemSearchProvider;

    Long memberId;
    Long certificateId;
    List<Long> examIds;
    List<Long> subjectIds;
    List<Exam> exams;
    List<Subject> subjects;
    List<Problem> problems1;
    List<Problem> problems2;
    List<Problem> problems3;
    List<Problem> problems4;
    List<Long> problemIds1;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        certificateId = 1L;
        Certificate certificate = createCertificate(certificateId);
        examIds = List.of(1L, 2L);
        subjectIds = List.of(3L, 4L);
        exams = examIds.stream().map(e -> createExam(e, certificate)).toList();
        subjects = subjectIds.stream().map(s -> createSubject(s, certificate)).toList();
        problems1 = LongStream.range(1, 21).mapToObj(i -> createProblem(i, certificate, exams.get(0), subjects.get(0))).toList();
        problems2 = LongStream.range(21, 41).mapToObj(i -> createProblem(i, certificate, exams.get(0), subjects.get(1))).toList();
        problems3 = LongStream.range(41, 61).mapToObj(i -> createProblem(i, certificate, exams.get(1), subjects.get(0))).toList();
        problems4 = LongStream.range(61, 81).mapToObj(i -> createProblem(i, certificate, exams.get(1), subjects.get(1))).toList();
        problemIds1 = problems1.stream().map(Problem::getId).collect(Collectors.toList());
    }

    @Test
    @DisplayName("문제 조회 시 중복된 과목 ID가 요청되는 경우 중복 제거 후 결과를 반환한다.")
    void givenDuplicateSubjectIds_whenGetProblemsBySubjectIds_thenRemoveDuplicateSubjectIds() {
        //given
        Long examId = examIds.get(0);
        List<Long> subjectIds = List.of(this.subjectIds.get(0), this.subjectIds.get(0));
        int count = 10;

        given(problemRepository.findDetailByExamIdAndSubjectIdWithBookmark(memberId, examIds.get(0), subjectIds.get(0), 20))
                .willReturn(problems1.stream().map(p -> createProblemWithBookmarkDetailQueryDto(p, true)).collect(Collectors.toList()));

        //when
        List<ProblemWithBookmarkDetailQueryDto> result = sut.findProblemsByExamIdAndSubjectIds(memberId, examId, subjectIds, count);

        //then
        result.forEach(dto -> assertThat(dto.subjectId()).isEqualTo(this.subjectIds.get(0)));
        result.forEach(dto -> assertThat(dto.examId()).isEqualTo(examId));
    }

    @Test
    @DisplayName("시험 조건이 없는 문제 조회 시 과목 마다 count 개수만큼 순서대로 문제를 반환한다.")
    void givenNoExamId_whenGetProblemsBySubjectIds_thenReturnProblemsByCount() {
        //given
        int count = 5;

        given(problemRepository.findDetailBySubjectIdWithBookmark(memberId, subjectIds.get(0)))
                .willReturn(problems1.stream().map(p -> createProblemWithBookmarkDetailQueryDto(p, true)).collect(Collectors.toList()));
        given(problemRepository.findDetailBySubjectIdWithBookmark(memberId, subjectIds.get(1)))
                .willReturn(problems2.stream().map(p -> createProblemWithBookmarkDetailQueryDto(p, true)).collect(Collectors.toList()));

        //when
        List<ProblemWithBookmarkDetailQueryDto> result = sut.findProblemsBySubjectId(memberId, subjectIds, count);

        //then
        result.subList(0, count)
                .forEach(dto -> assertThat(dto.subjectId()).isEqualTo(subjectIds.get(0)));
        result.subList(count, count * 2)
                .forEach(dto -> assertThat(dto.subjectId()).isEqualTo(subjectIds.get(1)));
    }

    @Test
    @DisplayName("문제 검색 시 검색된 문제가 없으면 빈 리스트를 반환한다.")
    void givenNoSearchResult_whenSearchProblem_thenReturnEmptyList() {
        //given
        String query = "검색어";
        Double lastScore = 1.0;
        Long lastId = 1L;

        given(problemSearchProvider.searchProblem(query, lastScore, lastId, certificateId, 10))
                .willReturn(List.of());
        given(problemRepository.findSummaryByIdsInWithBookmark(memberId, List.of()))
                .willReturn(List.of());

        //when
        List<ProblemWithBookmarkSummaryScoreQueryDto> result = sut.searchProblem(memberId, certificateId, query, lastScore, lastId);

        //then
        assertThat(result).isEmpty();
    }

}
