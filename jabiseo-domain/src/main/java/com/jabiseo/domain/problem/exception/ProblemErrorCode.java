package com.jabiseo.domain.problem.exception;

import com.jabiseo.domain.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ProblemErrorCode implements ErrorCode {

    PROBLEM_NOT_FOUND("문제를 찾을 수 없습니다.", "PRB_001", ErrorCode.NOT_FOUND),
    INVALID_PROBLEM_COUNT("문제의 개수가 올바르지 않습니다.", "PRB_002", ErrorCode.BAD_REQUEST),
    BOOKMARK_ALREADY_EXISTS("이미 북마크한 문제입니다.", "PRB_003", ErrorCode.BAD_REQUEST),
    BOOKMARK_NOT_FOUND("북마크 정보를 찾을 수 없습니다.", "PRB_004", ErrorCode.NOT_FOUND),
    DUPLICATED_SOLVING_PROBLEM("문제를 중복해서 풀 수 없습니다.", "PRB_005", ErrorCode.BAD_REQUEST),
    ;


    private final String message;
    private final String errorCode;
    private final int statusCode;

    ProblemErrorCode(String message, String errorCode, int statusCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}
