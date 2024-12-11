package com.logistcshub.user.infrastructure.common;

import lombok.Builder;

public record ApiResponse<T>(String status, String message, T data) {

    @Builder
    public ApiResponse(MessageType messageType, T data) {
        this("success", messageType.getMessage(), data);
    }
}
