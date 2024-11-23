package com.jabiseo.domain.problem.domain;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.domain.Exam;
import com.jabiseo.domain.certificate.domain.Subject;
import com.jabiseo.domain.common.config.JpaConfig;
import com.jabiseo.domain.common.config.QueryDslConfig;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.problem.repository.ProblemRepository;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkDetailQueryDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
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
@DisplayName("문제 세트에 대한 Problem Repository 테스트")
class ProblemRepositoryTest {

    @Autowired
    private ProblemRepository problemRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Long memberId;
    private List<Long> examIds;
    private List<Long> subjectIds;
    private List<Long> problemIds;
    private Certificate certificate;
    private List<Exam> exams;
    private List<Subject> subjects;
    private List<ProblemInfo> problemInfos;
    private int count;

    @BeforeEach
    void setUp() {
        //given
        Member member = createMember();
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

        List<Problem> requestProblems = List.of(
                createProblem(certificate, exams.get(0), subjects.get(0), getProblemInfo(examIds.get(0), subjectIds.get(0))),
                createProblem(certificate, exams.get(0), subjects.get(0), getProblemInfo(examIds.get(0), subjectIds.get(0))),
                createProblem(certificate, exams.get(0), subjects.get(2), getProblemInfo(examIds.get(0), subjectIds.get(2))),
                createProblem(certificate, exams.get(1), subjects.get(0), getProblemInfo(examIds.get(1), subjectIds.get(0)))
        );
        List<Bookmark> bookmarks = List.of(
                Bookmark.of(member, requestProblems.get(0)),
                Bookmark.of(member, requestProblems.get(3))
        );
        requestProblems.forEach(entityManager::persist);
        bookmarks.forEach(entityManager::persist);
        problemIds = requestProblems.stream().map(Problem::getId).toList();

        count = 10;
    }

    @Test
    @DisplayName("로그인한 유저가 시험, 과목 조건에 따라 문제 세트를 조회하는 쿼리가 정상적으로 동작한다.")
    void givenLoginMemberWithExamAndSubjectConditions_whenFindingProblems_thenFindProblems() {
        //when
        List<ProblemWithBookmarkDetailQueryDto> dtos = problemRepository.findDetailByExamIdAndSubjectIdWithBookmark(
                memberId, examIds.get(0), subjectIds.get(0), count
        );

        //then
        assertThat(dtos).hasSize(2);
        long trueCount = dtos.stream().filter(ProblemWithBookmarkDetailQueryDto::isBookmark).count();
        assertThat(trueCount).isEqualTo(1);
    }

    @Test
    @DisplayName("로그인한 유저가 시험을 제외한 과목 조건에 따라 문제 세트를 조회하는 쿼리가 정상적으로 동작한다.")
    void givenLoginMemberWithSubjectConditions_whenFindingProblems_thenFindProblems() {
        //when
        List<ProblemWithBookmarkDetailQueryDto> dtos = problemRepository.findDetailByExamIdAndSubjectIdWithBookmark(
                memberId, null, subjectIds.get(0), count
        );

        //then
        assertThat(dtos).hasSize(3);
        long trueCount = dtos.stream().filter(ProblemWithBookmarkDetailQueryDto::isBookmark).count();
        assertThat(trueCount).isEqualTo(2);
    }

    @Test
    @DisplayName("비로그인 유저가 시험, 과목 조건에 따라 문제 세트를 조회하는 쿼리가 정상적으로 동작한다.")
    void givenNonLoginMemberWithExamAndSubjectConditions_whenFindingProblems_thenFindProblems() {
        //when
        List<ProblemWithBookmarkDetailQueryDto> dtos = problemRepository.findDetailByExamIdAndSubjectIdWithBookmark(
                null, examIds.get(0), subjectIds.get(0), count
        );

        //then
        assertThat(dtos).hasSize(2);
        long trueCount = dtos.stream().filter(ProblemWithBookmarkDetailQueryDto::isBookmark).count();
        assertThat(trueCount).isEqualTo(0);
    }

    @Test
    @DisplayName("비로그인 유저가 시험을 제외한 과목 조건에 따라 문제 세트를 조회하는 쿼리가 정상적으로 동작한다.")
    void givenNonLoginMemberWithSubjectConditions_whenFindingProblems_thenFindProblems() {
        //when
        List<ProblemWithBookmarkDetailQueryDto> dtos = problemRepository.findDetailByExamIdAndSubjectIdWithBookmark(
                null, null, subjectIds.get(0), count
        );

        //then
        assertThat(dtos).hasSize(3);
        long trueCount = dtos.stream().filter(ProblemWithBookmarkDetailQueryDto::isBookmark).count();
        assertThat(trueCount).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 문제 id로 문제 세트를 조회하면 해당 문제는 제외된다.")
    void givenLoginMemberAndNonExistedProblemIds_whenFindingProblemsByIds_thenFindProblemsExceptThat() {
        //given
        List<Long> newProblemIds = new ArrayList<>(problemIds);
        newProblemIds.add(100L);

        //when
        List<ProblemWithBookmarkDetailQueryDto> dtos = problemRepository.findDetailByIdsInWithBookmark(
                memberId, newProblemIds
        );

        //then
        assertThat(dtos).hasSize(4);
        long trueCount = dtos.stream().filter(ProblemWithBookmarkDetailQueryDto::isBookmark).count();
        assertThat(trueCount).isEqualTo(2);
    }

    @Test
    @DisplayName("로그인한 유저가 문제 id로 문제 세트를 조회하는 쿼리가 정상적으로 동작한다.")
    void givenLoginMemberAndProblemIds_whenFindingProblemsByIds_thenFindProblems() {
        //when
        List<ProblemWithBookmarkDetailQueryDto> dtos = problemRepository.findDetailByIdsInWithBookmark(
                memberId, problemIds
        );

        //then
        assertThat(dtos).hasSize(4);
        long trueCount = dtos.stream().filter(ProblemWithBookmarkDetailQueryDto::isBookmark).count();
        assertThat(trueCount).isEqualTo(2);
    }

    @Test
    @DisplayName("비로그인 유저가 문제 id로 문제 세트를 조회하는 쿼리가 정상적으로 동작한다.")
    void givenNonLoginMemberAndProblemIds_whenFindingProblemsByIds_thenFindProblems() {
        //when
        List<ProblemWithBookmarkDetailQueryDto> dtos = problemRepository.findDetailByIdsInWithBookmark(
                null, problemIds
        );

        //then
        assertThat(dtos).hasSize(4);
        long trueCount = dtos.stream().filter(ProblemWithBookmarkDetailQueryDto::isBookmark).count();
        assertThat(trueCount).isEqualTo(0);
    }

    private ProblemInfo getProblemInfo(Long examId, Long subjectId) {
        return problemInfos.stream()
                .filter(problemInfo -> problemInfo.getExamId().equals(examId) && problemInfo.getSubjectId().equals(subjectId))
                .findFirst()
                .orElseThrow();
    }

}
