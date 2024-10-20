package com.jabiseo.domain.member.exception;

import com.jabiseo.domain.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND("회원을 찾을 수 없습니다.", "MEM_001", ErrorCode.NOT_FOUND),
    CURRENT_CERTIFICATE_NOT_EXIST("현재 자격증이 존재하지 않습니다.", "MEM_002", ErrorCode.BAD_REQUEST),
    ;

    private final String message;
    private final String errorCode;
    private final int statusCode;

    MemberErrorCode(String message, String errorCode, int statusCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}
