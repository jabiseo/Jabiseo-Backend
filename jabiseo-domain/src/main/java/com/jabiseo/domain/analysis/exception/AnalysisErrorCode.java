package com.jabiseo.domain.analysis.exception;

import com.jabiseo.domain.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum AnalysisErrorCode implements ErrorCode {

    CANNOT_CALCULATE_VULNERABILITY("취약점 분석을 할 수 없습니다.", "ANA_001", ErrorCode.INTERNAL_SERVER_ERROR),
    CANNOT_FIND_VECTOR("벡터를 찾을 수 없습니다.", "ANA_002", ErrorCode.INTERNAL_SERVER_ERROR),
    NOT_ENOUGH_SOLVED_PROBLEMS("문제를 충분히 풀지 않아서 분석할 수 없습니다.", "ANA_003", ErrorCode.BAD_REQUEST),
    CANNOT_ANALYSE_PROBLEM_SOLVING("분석할 수 없는 문제 풀이 기록입니다.", "ANA_004", ErrorCode.INTERNAL_SERVER_ERROR),
    ;

    private final String message;
    private final String errorCode;
    private final int statusCode;

    AnalysisErrorCode(String message, String errorCode, int statusCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}
