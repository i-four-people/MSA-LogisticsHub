package com.logistcshub.user.common.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;

@Builder(access = AccessLevel.PRIVATE)
public record ErrorResponse(
        boolean success,
        @NonNull
        String message
) implements CommonResponse {

    public static ErrorResponse of(String message) {

        return ErrorResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}