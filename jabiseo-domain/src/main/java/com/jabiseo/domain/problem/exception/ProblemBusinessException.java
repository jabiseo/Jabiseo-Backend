package com.jabiseo.domain.problem.exception;

import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.ErrorCode;

public class ProblemBusinessException extends BusinessException {

    public ProblemBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

}
