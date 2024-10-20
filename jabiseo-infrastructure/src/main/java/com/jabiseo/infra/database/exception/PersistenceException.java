package com.jabiseo.infra.database.exception;

import com.jabiseo.domain.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class PersistenceException extends RuntimeException {
    private final ErrorCode errorCode;

    public PersistenceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
