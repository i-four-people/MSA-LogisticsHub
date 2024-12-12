package com.logistics.order.presentation.response;

import com.logistics.order.presentation.exception.ErrorCode;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;

public record ErrorResponse(
        String status,
        String message,
        ErrorDetails error,
        LocalDateTime timestamp
) {
    public static ErrorResponse ERROR(String message, ErrorDetails error) {
        return new ErrorResponse(
                "failure",
                message,
                error,
                LocalDateTime.now()
        );
    }

    public static ErrorResponse from(ErrorCode errorCode, String urlPath) {
        return new ErrorResponse(
                "failure", // 기본 상태를 "failure"로 설정
                errorCode.name(), // ErrorCode의 메시지를 사용
                ErrorDetails.of(errorCode, errorCode.getMessage(), urlPath), // 기본 에러 세부사항 설정
                LocalDateTime.now() // 현재 시간 설정
        );
    }

    public record ErrorDetails(
            String code,
            String details,
            String path
    ) {
        public static ErrorDetails of(ErrorCode errorCode, String details, String path) {
            return new ErrorDetails(
                    errorCode.getCode(),
                    details,
                    path
            );
        }

        public static ErrorDetails of(ErrorCode errorCode, BindingResult bindingResult, String path) {
            return new ErrorDetails(
                    errorCode.getCode(),
                    bindingResult.getFieldErrors().isEmpty() ? "Validation error" : bindingResult.getFieldErrors().getFirst().getDefaultMessage(),
                    path
            );
        }
    }
}
