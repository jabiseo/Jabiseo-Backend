package com.jabiseo.domain.analysis.exception;

import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.ErrorCode;

public class AnalysisBusinessException extends BusinessException {

    public AnalysisBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

}
