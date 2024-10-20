package com.jabiseo.infra.client;

import com.jabiseo.domain.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum NetworkApiErrorCode implements ErrorCode {
    COMMON_API_FAIL("API fail", "NETWORK_001", ErrorCode.INTERNAL_SERVER_ERROR),
    KAKAO_JWK_API_FAIL("카카오 kauth jwk 연결 실패", "NETWORK_002", ErrorCode.INTERNAL_SERVER_ERROR),
    GOOGLE_OPENAI_CONFIG_API_FAIL("구글 openai 연결 실패", "NETWORK_003", ErrorCode.INTERNAL_SERVER_ERROR),
    GOOGLE_JWK_API_FAIL("구글 jwk 연결 실패", "NETWORK_004", ErrorCode.INTERNAL_SERVER_ERROR),
    S3_UPLOAD_FAIL("s3 upload 실패", "NETWORK_005", ErrorCode.INTERNAL_SERVER_ERROR),
    OPEN_SEARCH_API_FAIL("opensearch 연결 실패", "NETWORK_006", ErrorCode.INTERNAL_SERVER_ERROR),
    ;

    private final String message;
    private final String errorCode;
    private final int statusCode;

    NetworkApiErrorCode(String message, String errorCode, int statusCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}
