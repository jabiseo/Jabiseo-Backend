package com.jabiseo.api.exception;

import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.CommonErrorCode;
import com.jabiseo.domain.common.exception.ErrorCode;
import com.jabiseo.infra.database.exception.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException e) {
        log.info(e.getMessage());
        ErrorCode code = e.getErrorCode();
        return ResponseEntity
                .status(code.getStatusCode())
                .body(new ErrorResponse(code.getMessage(), code.getErrorCode()));
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<?> handlePersistenceException(PersistenceException e) {
        log.info(e.getMessage());
        ErrorCode code = e.getErrorCode();
        return ResponseEntity
                .status(code.getStatusCode())
                .body(new ErrorResponse(code.getMessage(), code.getErrorCode()));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> handleMethodValidationException(HandlerMethodValidationException e) {
        ErrorCode errorCode = CommonErrorCode.INVALID_REQUEST_PARAMETER;
        String errorMessage;
        if (e.getDetailMessageArguments() == null || e.getDetailMessageArguments().length == 0) {
            errorMessage = errorCode.getMessage();
        } else {
            errorMessage = e.getDetailMessageArguments()[0].toString();
        }

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(new ErrorResponse(errorMessage, errorCode.getErrorCode()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        ErrorCode errorCode = CommonErrorCode.INVALID_REQUEST_PARAMETER;
        log.error(e.getMessage());
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(new ErrorResponse(errorCode.getMessage(), errorCode.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info(e.getMessage());
        ErrorCode errorCode = CommonErrorCode.INVALID_REQUEST_BODY;
        StringBuilder errors = new StringBuilder();
        e.getBindingResult()
                .getFieldErrors()
                .forEach((er) -> errors.append(er.getDefaultMessage()));

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(new ErrorResponse(errors.toString(), errorCode.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e.getMessage());
        String errorMessage = e.getMessage() +
                              " " +
                              CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage();
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(errorMessage, CommonErrorCode.INTERNAL_SERVER_ERROR.getErrorCode()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNotFound(NoHandlerFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("URL에 해당하는 요청을 찾을 수 없습니다", CommonErrorCode.NOT_FOUND.getErrorCode()));
    }

}
