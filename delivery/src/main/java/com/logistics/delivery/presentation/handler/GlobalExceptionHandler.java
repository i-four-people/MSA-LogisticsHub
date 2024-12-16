package com.logistics.delivery.presentation.handler;

import com.logistics.delivery.presentation.exception.BusinessException;
import com.logistics.delivery.presentation.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("BusinessException occurred: {}", e.getMessage());
        ErrorResponse.ErrorDetails errorDetails = ErrorResponse.ErrorDetails.of(
                e.getErrorCode(), e.getMessage(), request.getRequestURI());
        ErrorResponse errorResponse = ErrorResponse.ERROR(e.getErrorCode().toString(), errorDetails);

        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
    }

}
