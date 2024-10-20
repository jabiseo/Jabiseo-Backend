package com.jabiseo.domain.auth.exception;

import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.ErrorCode;

public class AuthenticationBusinessException extends BusinessException {

    public AuthenticationBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
