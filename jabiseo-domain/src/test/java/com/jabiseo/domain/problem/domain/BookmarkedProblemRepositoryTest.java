package com.jabiseo.domain.problem.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.domain.Exam;
import com.jabiseo.domain.certificate.domain.Subject;
import com.jabiseo.domain.common.config.JpaConfig;
import com.jabiseo.domain.common.config.QueryDslConfig;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.problem.repository.ProblemRepository;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkSummaryQueryDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fixture.CertificateFixture.createCertificate;
import static fixture.ExamFixture.createExam;
import static fixture.MemberFixture.createMember;
import static fixture.ProblemFixture.createProblem;
import static fixture.ProblemInfoFixture.createProblemInfo;
import static fixture.SubjectFixture.createSubject;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({JpaConfig.class, QueryDslConfig.class})
@DisplayName("북마크에 대한 Problem Repository 테스트")
class BookmarkedProblemRepositoryTest {

    @Autowired
    private ProblemRepository problemRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Long memberId;
    private Member member;
    private List<Long> examIds;
    private List<Long> subjectIds;
    private Certificate certificate;
    private List<Exam> exams;
    private List<Subject> subjects;
    private List<ProblemInfo> problemInfos;

    @BeforeEach
    void setUp() {
        //given
        member = createMember();
        certificate = createCertificate();
        member.updateCurrentCertificate(certificate);
        exams = new ArrayList<>();
        IntStream.range(0, 2).forEach(i -> exams.add(createExam(certificate)));
        subjects = new ArrayList<>();
        IntStream.range(0, 3).forEach(i -> subjects.add(createSubject(certificate)));

        entityManager.persist(member);
        entityManager.persist(certificate);
        exams.forEach(entityManager::persist);
        subjects.forEach(entityManager::persist);

        examIds = exams.stream().map(Exam::getId).toList();
        subjectIds = subjects.stream().map(Subject::getId).toList();
        memberId = member.getId();

        problemInfos = IntStream.range(0, examIds.size())
                .boxed()
                .flatMap(examIndex -> IntStream.range(0, subjectIds.size())
                        .mapToObj(subjectIndex -> createProblemInfo(
                                (long) examIndex * subjectIds.size() + subjectIndex + 1,  // ID를 고유하게 생성
                                certificate.getId(),
                                examIds.get(examIndex),
                                subjectIds.get(subjectIndex))))
                .collect(Collectors.toCollection(ArrayList::new));
        problemInfos.forEach(entityManager::persist);
    }

    @Test
    @DisplayName("시험, 과목 조건에 따라 북마크된 문제를 조회하는 쿼리가 정상적으로 동작한다.")
    void givenExamAndSubjectConditions_whenFindingBookmarkedProblems_thenFindBookmarkedProblems() {
        //given
        List<Problem> requestProblems = List.of(
                createProblem(certificate, exams.get(0), subjects.get(0), getProblemInfo(examIds.get(0), subjectIds.get(0))),
                createProblem(certificate, exams.get(0), subjects.get(1), getProblemInfo(examIds.get(0), subjectIds.get(1))),
                createProblem(certificate, exams.get(0), subjects.get(2), getProblemInfo(examIds.get(0), subjectIds.get(2))),
                createProblem(certificate, exams.get(1), subjects.get(0), getProblemInfo(examIds.get(1), subjectIds.get(0)))
        );
        List<Bookmark> bookmarks = requestProblems.stream()
                .map(problem -> Bookmark.of(member, problem))
                .toList();
        requestProblems.forEach(entityManager::persist);
        bookmarks.forEach(entityManager::persist);
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<ProblemWithBookmarkSummaryQueryDto> dtos = problemRepository.findBookmarkedSummaryByExamIdAndSubjectIdsInWithBookmark(
                memberId, examIds.get(0), List.of(subjectIds.get(0), subjectIds.get(1)), pageable
        );

        //then
        assertThat(dtos).hasSize(2);
        dtos.forEach(dto -> assertThat(dto.isBookmark()).isTrue());
    }

    @Test
    @DisplayName("시험 조건은 없고 과목에 따라 북마크된 문제를 조회하는 쿼리가 정상적으로 동작한다.")
    void givenSubjectConditions_whenFindingBookmarkedProblems_thenFindBookmarkedProblems() {
        //given
        List<Problem> requestProblems = List.of(
                createProblem(certificate, exams.get(0), subjects.get(0), getProblemInfo(examIds.get(0), subjectIds.get(0))),
                createProblem(certificate, exams.get(0), subjects.get(1), getProblemInfo(examIds.get(0), subjectIds.get(1))),
                createProblem(certificate, exams.get(0), subjects.get(2), getProblemInfo(examIds.get(0), subjectIds.get(2))),
                createProblem(certificate, exams.get(1), subjects.get(0), getProblemInfo(examIds.get(1), subjectIds.get(0)))
        );
        List<Bookmark> bookmarks = requestProblems.stream()
                .map(problem -> Bookmark.of(member, problem))
                .toList();
        requestProblems.forEach(entityManager::persist);
        bookmarks.forEach(entityManager::persist);
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<ProblemWithBookmarkSummaryQueryDto> dtos = problemRepository.findBookmarkedSummaryByExamIdAndSubjectIdsInWithBookmark(
                memberId, null, List.of(subjectIds.get(0), subjectIds.get(1)), pageable
        );

        //then
        assertThat(dtos).hasSize(3);
        dtos.forEach(dto -> assertThat(dto.isBookmark()).isTrue());
    }

    @ParameterizedTest
    @DisplayName("페이지 수가 2개 이상인 경우, 시험, 과목 조건에 따라 북마크된 문제가 정상적으로 동작한다.")
    @CsvSource({"0, 10", "1, 5"})
    void givenExamAndSubjectConditionsWithMultiplePage_whenFindingBookmarkedProblems_thenFindBookmarkedProblems(int page, int pageSize) {
        //given
        List<Problem> requestProblems = IntStream.range(0, 15)
                .mapToObj(id -> createProblem(certificate, exams.get(0), subjects.get(0), getProblemInfo(examIds.get(0), subjectIds.get(0))))
                .toList();
        List<Bookmark> bookmarks = requestProblems.stream()
                .map(problem -> Bookmark.of(member, problem))
                .toList();
        requestProblems.forEach(entityManager::persist);
        bookmarks.forEach(entityManager::persist);
        Pageable pageable = PageRequest.of(page, 10);

        //when
        Page<ProblemWithBookmarkSummaryQueryDto> dtos = problemRepository.findBookmarkedSummaryByExamIdAndSubjectIdsInWithBookmark(
                memberId, null, List.of(subjectIds.get(0), subjectIds.get(0)), pageable
        );

        //then
        assertThat(dtos).hasSize(pageSize);
        dtos.forEach(dto -> assertThat(dto.isBookmark()).isTrue());
    }

    private ProblemInfo getProblemInfo(Long examId, Long subjectId) {
        return problemInfos.stream()
                .filter(problemInfo -> problemInfo.getExamId().equals(examId) && problemInfo.getSubjectId().equals(subjectId))
                .findFirst()
                .orElseThrow();
    }

}
