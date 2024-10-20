package com.jabiseo.domain.auth.exception;

import com.jabiseo.domain.common.exception.ErrorCode;

public enum AuthenticationErrorCode implements ErrorCode {

    INVALID_ID_TOKEN("잘못된 idToken 입니다.", "AUTH_001", ErrorCode.UNAUTHORIZED),
    EXPIRED_ID_TOKEN("만료된 idToken 입니다.", "AUTH_002", ErrorCode.UNAUTHORIZED),
    NOT_SUPPORT_OAUTH("지원하지 않는 oauth 인증 수단입니다", "AUTH_003", ErrorCode.UNAUTHORIZED),
    EXPIRED_APP_JWT("만료된 jwt 토큰 입니다", "AUTH_004", ErrorCode.UNAUTHORIZED),
    INVALID_APP_JWT("잘못된 jwt 토큰입니다", "AUTH_005", ErrorCode.UNAUTHORIZED),
    GET_JWK_FAIL("jwk 획득 실패", "AUTH_006", ErrorCode.INTERNAL_SERVER_ERROR),
    REQUIRE_LOGIN("로그인이 필요합니다","AUTH007", ErrorCode.UNAUTHORIZED),
    NOT_MATCH_REFRESH("refresh token이 일치하지 않습니다", "AUTH_008", ErrorCode.UNAUTHORIZED);

    private final String message;
    private final String errorCode;
    private final int statusCode;

    AuthenticationErrorCode(String message, String errorCode, int statusCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }


    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }
}
