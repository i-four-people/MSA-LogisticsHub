package com.logistcshub.hub.common.exception;

import com.logistcshub.hub.common.exception.application.dtos.ErrorResponse;
import com.logistcshub.hub.common.exception.application.type.ErrorCode;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("RestApiException occurred : ErrorCode = {} message = {}",
                errorCode.name(), errorCode.getDescription());
        return handleExceptionInternal(errorCode);
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponseBody(errorCode));
    }

    private ErrorResponse makeErrorResponseBody(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getDescription())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
