package com.jabiseo.domain.common.exception;

import lombok.Getter;

@Getter
public enum CommonErrorCode implements ErrorCode {

    INVALID_REQUEST_BODY("요청의 body가 올바르지 않습니다.", "COM_001", ErrorCode.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("internal server error", "COM_002", ErrorCode.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST_PARAMETER("요청의 parameter가 올바르지 않습니다.", "COM_003", ErrorCode.BAD_REQUEST),
    FORBIDDEN("권한이 없거나 금지된 요청입니다.", "COM_004", ErrorCode.FORBIDDEN),
    NOT_FOUND("리소스를 찾을 수 없습니다", "COM_005", ErrorCode.NOT_FOUND);

    private final String message;
    private final String errorCode;
    private final int statusCode;

    CommonErrorCode(String message, String errorCode, int statusCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

}
