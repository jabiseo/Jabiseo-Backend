package com.jabiseo.domain.learning.service;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.learning.domain.Learning;
import com.jabiseo.domain.learning.domain.LearningMode;
import com.jabiseo.domain.learning.repository.LearningRepository;
import com.jabiseo.domain.learning.dto.TodayLearningDto;
import com.jabiseo.domain.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static fixture.CertificateFixture.createCertificate;
import static fixture.LearningFixture.createLearning;
import static fixture.MemberFixture.createMember;
import static fixture.ProblemSolvingFixture.createProblemSolving;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@DisplayName("결과 제출 관련 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class LearningServiceTest {

    @InjectMocks
    LearningService sut;

    @Mock
    LearningRepository learningRepository;

    private Member member;
    private Certificate certificate;

    @BeforeEach
    void setUp() {
        Long memberId = 1L;
        member = createMember(memberId);
        Long certificateId = 2L;
        certificate = createCertificate(certificateId);
    }

    @Test
    @DisplayName("아무 문제도 풀지 않으면 모든 결과가 0이어야 한다.")
    void givenNoProblemSolving_whenFindTodayLearning_thenAllResultIsZero() {
        // given
        given(learningRepository.findByMemberAndCreatedAtBetweenWithProblemSolvings(eq(member), any(), any())).willReturn(List.of());

        // when
        TodayLearningDto todayLearningDto = sut.findTodayLearning(member);

        // then
        assertThat(todayLearningDto.studyModeCount()).isZero();
        assertThat(todayLearningDto.studyModeCorrectRate()).isZero();
        assertThat(todayLearningDto.examModeCount()).isZero();
        assertThat(todayLearningDto.examModeCorrectRate()).isZero();
    }

    @Test
    @DisplayName("공부 모드에서 문제를 풀었을 때 정답률이 계산되어야 한다.")
    void givenStudyModeProblemSolving_whenFindTodayLearning_thenCalculateStudyModeCorrectRate() {
        // given
        List<Learning> learnings = IntStream.range(1, 4)
                .mapToObj(id -> createLearning((long) id, certificate, LearningMode.STUDY))
                .toList();
        createProblemSolving(1L, learnings.get(0), true);
        createProblemSolving(2L, learnings.get(0), false);
        createProblemSolving(3L, learnings.get(1), false);
        createProblemSolving(4L, learnings.get(2), true);
        createProblemSolving(5L, learnings.get(2), true);
        createProblemSolving(6L, learnings.get(2), false);
        createProblemSolving(7L, learnings.get(2), false);
        given(learningRepository.findByMemberAndCreatedAtBetweenWithProblemSolvings(eq(member), any(), any()))
                .willReturn(learnings);

        // when
        TodayLearningDto todayLearningDto = sut.findTodayLearning(member);

        // then
        assertThat(todayLearningDto.studyModeCount()).isEqualTo(3);
        assertThat(todayLearningDto.studyModeCorrectRate()).isEqualTo(43); // 3/7 correct
        assertThat(todayLearningDto.examModeCount()).isZero();
        assertThat(todayLearningDto.examModeCorrectRate()).isZero();
    }

    @Test
    @DisplayName("시험 모드에서 문제를 풀었을 때 정답률이 계산되어야 한다.")
    void givenExamModeProblemSolving_whenFindTodayLearning_thenCalculateExamModeCorrectRate() {
        // given
        List<Learning> learnings = IntStream.range(1, 5)
                .mapToObj(id -> createLearning((long) id, certificate, LearningMode.EXAM))
                .toList();
        createProblemSolving(1L, learnings.get(0), true);
        createProblemSolving(2L, learnings.get(0), true);
        createProblemSolving(3L, learnings.get(1), true);
        createProblemSolving(4L, learnings.get(1), true);
        createProblemSolving(5L, learnings.get(2), true);
        createProblemSolving(6L, learnings.get(3), false);
        createProblemSolving(7L, learnings.get(3), true);
        given(learningRepository.findByMemberAndCreatedAtBetweenWithProblemSolvings(eq(member), any(), any()))
                .willReturn(learnings);

        // when
        TodayLearningDto todayLearningDto = sut.findTodayLearning(member);

        // then
        assertThat(todayLearningDto.studyModeCount()).isZero();
        assertThat(todayLearningDto.studyModeCorrectRate()).isZero();
        assertThat(todayLearningDto.examModeCount()).isEqualTo(4);
        assertThat(todayLearningDto.examModeCorrectRate()).isEqualTo(86); // 6/7 correct
    }

    @Test
    @DisplayName("공부 모드와 시험 모드에서 문제를 풀었을 때 정답률이 계산되어야 한다.")
    void givenStudyAndExamModeProblemSolving_whenFindTodayLearning_thenCalculateCorrectRate() {
        // given
        List<Learning> learnings = IntStream.range(1, 5)
                .mapToObj(id -> createLearning((long) id, certificate, id % 2 == 0 ? LearningMode.STUDY : LearningMode.EXAM))
                .toList();
        createProblemSolving(1L, learnings.get(0), true);
        createProblemSolving(2L, learnings.get(0), false);
        createProblemSolving(3L, learnings.get(1), true);
        createProblemSolving(4L, learnings.get(1), true);
        createProblemSolving(5L, learnings.get(2), true);
        createProblemSolving(6L, learnings.get(3), false);
        createProblemSolving(7L, learnings.get(3), true);
        given(learningRepository.findByMemberAndCreatedAtBetweenWithProblemSolvings(eq(member), any(), any()))
                .willReturn(learnings);

        // when
        TodayLearningDto todayLearningDto = sut.findTodayLearning(member);

        // then
        assertThat(todayLearningDto.studyModeCount()).isEqualTo(2);
        assertThat(todayLearningDto.studyModeCorrectRate()).isEqualTo(75); // 3/4 correct
        assertThat(todayLearningDto.examModeCount()).isEqualTo(2);
        assertThat(todayLearningDto.examModeCorrectRate()).isEqualTo(67); // 2/3 correct
    }
}
