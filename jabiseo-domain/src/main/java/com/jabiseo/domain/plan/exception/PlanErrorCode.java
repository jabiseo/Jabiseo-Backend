package com.jabiseo.domain.plan.exception;

import com.jabiseo.domain.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum PlanErrorCode implements ErrorCode {

    ALREADY_EXIST_PLAN("이미 진행중인 플랜이 있습니다", "PLAN_001", ErrorCode.CONFLICT),
    NOT_FOUND_PLAN("플랜을 찾을 수 없습니다", "PLAN_002", ErrorCode.NOT_FOUND),
    IS_NOT_OWNER("플랜 주인이 아닙니다", "PLAN_003", ErrorCode.FORBIDDEN);

    PlanErrorCode(String message, String errorCode, int statusCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    private final String message;
    private final String errorCode;
    private final int statusCode;
}
