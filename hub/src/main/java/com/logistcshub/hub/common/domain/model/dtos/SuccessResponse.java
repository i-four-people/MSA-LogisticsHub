package com.logistcshub.hub.common.domain.model.dtos;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.logistcshub.hub.common.domain.model.type.ResponseMessage;
import lombok.Builder;

@Builder
public record SuccessResponse<T>(
        String code,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL) T data
) {

    public static <T> SuccessResponse<T> of(ResponseMessage message, T data) {
        return SuccessResponse.<T>builder()
                .code(message.getStatus().name())
                .message(message.getMessage())
                .data(data)
                .build();
    }

    public static <T> SuccessResponse<T> of(ResponseMessage message) {
        return SuccessResponse.<T>builder()
                .code(message.getStatus().name())
                .message(message.getMessage())
                .build();
    }
}
