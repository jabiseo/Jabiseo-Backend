package com.jabiseo.domain.plan.exception;

import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.ErrorCode;

public class PlanBusinessException extends BusinessException {

    public PlanBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
