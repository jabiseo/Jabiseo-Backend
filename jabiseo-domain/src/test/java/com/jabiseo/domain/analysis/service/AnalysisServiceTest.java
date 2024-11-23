package com.jabiseo.domain.analysis.service;

import com.jabiseo.domain.analysis.exception.AnalysisBusinessException;
import com.jabiseo.domain.analysis.exception.AnalysisErrorCode;
import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.learning.domain.ProblemSolving;
import com.jabiseo.domain.learning.repository.ProblemSolvingRepository;
import com.jabiseo.domain.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static fixture.CertificateFixture.createCertificate;
import static fixture.LearningFixture.createLearning;
import static fixture.MemberFixture.createMember;
import static fixture.ProblemFixture.createProblem;
import static fixture.ProblemSolvingFixture.createProblemSolving;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("분석 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

    @InjectMocks
    AnalysisService sut;

    @Mock
    ProblemSolvingRepository problemSolvingRepository;

    @Mock
    VulnerabilityProvider vulnerabilityProvider;

    private Member member;

    private Certificate certificate;

    @BeforeEach
    void setupMemberAndCertificate() {
        Long memberId = 1L;
        Long certificateId = 2L;
        this.member = createMember(memberId);
        this.certificate = createCertificate(certificateId);
    }

    @Test
    @DisplayName("[findVulnerableVector 테스트] 문제 풀이 분석의 가장 긴 기간인 90일 이내의 문제 풀이를 500건까지 조회한다.")
    void givenProblemSolvings_whenFindVulnerableVector_thenFindProblemSolvings() {
        //given
        Long problemId = 3L;
        List<ProblemSolving> problemSolvings = List.of(
                createProblemSolving(1L, member, createProblem(problemId), createLearning(4L, certificate, now()), true)
        );

        // 90일 전의 날짜 계산
        LocalDateTime ninetyDaysAgo = now().minusDays(90);
        PageRequest pageRequest = PageRequest.of(0, 500);

        given(problemSolvingRepository.findWithLearningByCreatedAtAfterOrderByCreatedAtDesc(
                eq(member), eq(certificate), any(LocalDateTime.class), any(PageRequest.class)))
                .willReturn(problemSolvings);
        given(vulnerabilityProvider.findVectorsOfProblems(any(), any()))
                .willReturn(Map.of(problemId, List.of(1.0f, 2.0f, 3.0f)));

        //when
        sut.findVulnerableVector(member, certificate);

        //then
        ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(problemSolvingRepository).findWithLearningByCreatedAtAfterOrderByCreatedAtDesc(
                eq(member), eq(certificate), dateCaptor.capture(), pageRequestCaptor.capture());
        // 검증: 90일 전의 날짜인지 확인
        assertThat(dateCaptor.getValue()).isAfterOrEqualTo(ninetyDaysAgo);
        // 검증: PageRequest가 500건인지 확인
        assertThat(pageRequestCaptor.getValue()).isEqualTo(pageRequest);
    }


    @Test
    @DisplayName("[findVulnerableVector 테스트] 90일 이내에 문제를 푼 기록이 없으면 예외처리한다.")
    void givenNoProblemSolving_whenFindVulnerableVector_thenThrowException() {
        //given
        given(problemSolvingRepository.findWithLearningByCreatedAtAfterOrderByCreatedAtDesc(eq(member), eq(certificate), any(LocalDateTime.class), any(PageRequest.class)))
                .willReturn(List.of());

        //when, then
        assertThatThrownBy(() -> sut.findVulnerableVector(member, certificate))
                .isInstanceOf(AnalysisBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", AnalysisErrorCode.NOT_ENOUGH_SOLVED_PROBLEMS);
    }

    @ParameterizedTest
    @DisplayName("[calculateWeight 테스트] 문제 풀이의 가중치를 계산한다. 맞은 문제는 타입 가중치에 -1을 곱한다.")
    @CsvSource(value = {
            "1, 2, true, -0.5",
            "2, 219, false, 0.3",
            "19, 70, true, -0.3"
    })
    void givenProblemSolving_whenCalculateWeight_thenCalculateWeight(int daysAgo, int period, boolean isCorrect, double expected) {
        //given
        ProblemSolving problemSolving = createProblemSolving(1L, member, createProblem(1L), createLearning(4L, certificate, now().minusDays(daysAgo)), isCorrect);

        //when
        double weight = sut.calculateWeight(problemSolving, period);

        //then
        assertThat(weight).isEqualTo(expected);
    }

    @Test
    @DisplayName("[calculateWeightsOfProblems 테스트] 문제 풀이들의 문제 ID별 가중치를 계산해 Map으로 반환한다.")
    void givenProblemSolvings_whenCalculateWeightsOfProblems_thenCalculateWeights() {
        //given
        List<Long> problemIds = List.of(1L, 2L);
        List<Integer> daysAgo = List.of(2, 19, 70);
        List<ProblemSolving> problemSolvings = List.of(
                createProblemSolving(1L, member, createProblem(problemIds.get(0)), createLearning(4L, certificate, now().minusDays(daysAgo.get(0))), true),
                createProblemSolving(2L, member, createProblem(problemIds.get(0)), createLearning(5L, certificate, now().minusDays(daysAgo.get(1))), false),
                createProblemSolving(3L, member, createProblem(problemIds.get(1)), createLearning(6L, certificate, now().minusDays(daysAgo.get(2))), true)
        );

        //when
        Map<Long, Double> problemIdToWeight = sut.calculateWeightsOfProblems(problemSolvings);

        //then
        assertThat(problemIdToWeight).contains(
                Map.entry(problemIds.get(0), -0.5 + 0.3),
                Map.entry(problemIds.get(1), -0.2)
        );
    }

    @Test
    @DisplayName("[calculateVulnerableVector 테스트] 문제 ID별로 가중치를 곱하여 더한 후 반환한다.")
    void givenProblemSolvings_whenCalculateVulnerableVector_thenSumWeightedVectors() {
        //given
        List<Long> distinctProblemIds = List.of(1L, 2L, 3L);
        Map<Long, List<Float>> problemIdToVector = Map.of(
                distinctProblemIds.get(0), List.of(1.0f, 2.0f, 3.0f),
                distinctProblemIds.get(1), List.of(4.0f, 5.0f, 6.0f),
                distinctProblemIds.get(2), List.of(7.0f, 8.0f, 9.0f)
        );
        Map<Long, Double> problemIdToWeight = Map.of(
                distinctProblemIds.get(0), 1.1,
                distinctProblemIds.get(1), 2.2,
                distinctProblemIds.get(2), 3.3
        );

        //when
        List<Float> vulnerableVector = sut.calculateVulnerableVector(distinctProblemIds, problemIdToVector, problemIdToWeight);

        //then
        assertThat(vulnerableVector.get(0)).isEqualTo(1.1f + 8.8f + 23.1f);
        assertThat(vulnerableVector.get(1)).isEqualTo(2.2f + 11.0f + 26.4f);
        assertThat(vulnerableVector.get(2)).isEqualTo(3.3f + 13.2f + 29.7f);
    }
}
