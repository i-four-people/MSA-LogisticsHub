package com.logistics.delivery.presentation.response;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        String status,
        String message,
        T data,
        LocalDateTime timestamp
) {

    public static <T> ApiResponse<T> success(MessageType messageType, T data) {
        return new ApiResponse<>(
                "success",
                messageType.getMessage(),
                data,
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> success(MessageType messageType) {
        return new ApiResponse<>(
                "success",
                messageType.getMessage(),
                null,
                LocalDateTime.now()
        );
    }

}
