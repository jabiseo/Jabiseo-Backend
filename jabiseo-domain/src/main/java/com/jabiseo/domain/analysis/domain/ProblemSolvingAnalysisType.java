package com.jabiseo.domain.analysis.domain;

import com.jabiseo.domain.analysis.exception.AnalysisBusinessException;
import com.jabiseo.domain.analysis.exception.AnalysisErrorCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;

@Getter
public enum ProblemSolvingAnalysisType {

    // maxPeriodDay가 커지면 maxCount도 증가해야 함
    // 단기 분석 타입: 최근 14일 이내에 최대 200개의 문제 풀이에 가중치 0.5 적용
    SHORT_TERM( 0.5, 14, 200),
    // 중기 분석 타입: 최근 30일 이내에 최대 300개의 문제 풀이에 가중치 0.3 적용
    MID_TERM( 0.3, 30, 300),
    // 장기 분석 타입: 최근 90일 이내에 최대 500개의 문제 풀이에 가중치 0.2 적용
    LONG_TERM( 0.2, 90, 500);

    final double weight;
    final int maxPeriodDay;
    final int maxCount;

    ProblemSolvingAnalysisType(double weight, int maxPeriodDay, int maxCount) {
        this.weight = weight;
        this.maxPeriodDay = maxPeriodDay;
        this.maxCount = maxCount;
    }

    public static ProblemSolvingAnalysisType getLongestPeriodAnalysisType() {
        return LONG_TERM;
    }

    // 푼 기간과 최근 푼 순서가 주어지면 그에 맞는 ProblemSolvingAnalysisType을 반환한다.
    public static ProblemSolvingAnalysisType fromPeriodAndCount(int period, int sequence) {
        return Arrays.stream(ProblemSolvingAnalysisType.values())
                .sorted(Comparator.comparingInt(ProblemSolvingAnalysisType::getMaxPeriodDay))
                .filter(type -> period <= type.getMaxPeriodDay() && sequence <= type.getMaxCount())
                .findFirst()
                .orElseThrow(() -> new AnalysisBusinessException(AnalysisErrorCode.CANNOT_ANALYSE_PROBLEM_SOLVING));
    }

}
