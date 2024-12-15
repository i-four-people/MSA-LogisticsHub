package com.logistcshub.user.common.response;

import com.logistcshub.user.common.message.MessageType;
import lombok.Builder;

public record ApiResponse<T>(String status, String message, T data) {

    @Builder
    public ApiResponse(MessageType messageType, T data) {
        this("success", messageType.getMessage(), data);
    }
}
