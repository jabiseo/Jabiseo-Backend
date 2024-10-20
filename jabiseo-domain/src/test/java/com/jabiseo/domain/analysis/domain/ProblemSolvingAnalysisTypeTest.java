package com.jabiseo.domain.analysis.domain;

import com.jabiseo.domain.analysis.domain.ProblemSolvingAnalysisType;
import com.jabiseo.domain.analysis.exception.AnalysisBusinessException;
import com.jabiseo.domain.analysis.exception.AnalysisErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ProblemSolvingAnalysisType 테스트")
@ExtendWith(MockitoExtension.class)
class ProblemSolvingAnalysisTypeTest {

    @DisplayName("기간과 문제 수에 따른 분석 타입을 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "0, 0, SHORT_TERM",
            "13, 199, SHORT_TERM",
            "14, 200, SHORT_TERM",
            "30, 300, MID_TERM",
            "90, 500, LONG_TERM"
    })
    void fromPeriodAndCount(int period, int count, ProblemSolvingAnalysisType expected) {
        // when
        ProblemSolvingAnalysisType actual = ProblemSolvingAnalysisType.fromPeriodAndCount(period, count);

        // then
        assertEquals(expected, actual);
    }

    @DisplayName("기간과 문제 수가 올바르지 않을 경우 예외처리한다.")
    @ParameterizedTest
    @CsvSource({
            "91, 100",
            "89, 501",
            "91, 501"
    })
    void fromPeriodAndCountWithDefault(int period, int count) {
        // when & then
        assertThatThrownBy(() -> ProblemSolvingAnalysisType.fromPeriodAndCount(period, count))
                .isInstanceOf(AnalysisBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", AnalysisErrorCode.CANNOT_ANALYSE_PROBLEM_SOLVING);
    }
}
