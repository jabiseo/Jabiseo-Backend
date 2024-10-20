package com.jabiseo.domain.member.exception;

import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.ErrorCode;

public class MemberBusinessException extends BusinessException {

        public MemberBusinessException(ErrorCode errorCode) {
            super(errorCode);
        }

}
