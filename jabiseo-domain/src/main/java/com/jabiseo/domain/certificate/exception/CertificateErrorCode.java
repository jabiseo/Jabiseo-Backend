package com.jabiseo.domain.certificate.exception;

import com.jabiseo.domain.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum CertificateErrorCode implements ErrorCode {

    CERTIFICATE_NOT_FOUND("자격증을 찾을 수 없습니다.", "CER_001", ErrorCode.NOT_FOUND),
    SUBJECT_NOT_FOUND_IN_CERTIFICATE("자격증에서 해당 과목을 찾을 수 없습니다.", "CER_002", ErrorCode.NOT_FOUND),
    EXAM_NOT_FOUND_IN_CERTIFICATE("자격증에서 해당 시험을 찾을 수 없습니다.", "CER_003", ErrorCode.NOT_FOUND),
    PROBLEM_NOT_FOUND_IN_CERTIFICATE("자격증에서 해당 문제를 찾을 수 없습니다.", "CER_004", ErrorCode.NOT_FOUND),
    ;


    private final String message;
    private final String errorCode;
    private final int statusCode;

    CertificateErrorCode(String message, String errorCode, int statusCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}
