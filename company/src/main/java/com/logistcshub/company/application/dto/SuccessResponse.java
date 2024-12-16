package com.logistcshub.company.application.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
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
