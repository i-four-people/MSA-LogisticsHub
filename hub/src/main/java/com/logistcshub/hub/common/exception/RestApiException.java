package com.logistcshub.hub.common.exception;

import com.logistcshub.hub.common.exception.application.type.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {
    private final ErrorCode errorCode;
}
