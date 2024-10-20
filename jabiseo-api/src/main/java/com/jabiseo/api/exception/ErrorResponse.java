package com.jabiseo.api.exception;

import com.jabiseo.domain.common.exception.ErrorCode;

public record ErrorResponse(
        String message,
        String errorCode
) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getMessage(), errorCode.getErrorCode());
    }
}
