package com.jabiseo.infra.client;

import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.ErrorCode;

public class NetworkApiException extends BusinessException {

    public NetworkApiException(ErrorCode errorCode) {
        super(errorCode);
    }
}
