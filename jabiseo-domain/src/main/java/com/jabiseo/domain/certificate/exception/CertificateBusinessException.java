package com.jabiseo.domain.certificate.exception;

import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.ErrorCode;

public class CertificateBusinessException extends BusinessException {

    public CertificateBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

}
