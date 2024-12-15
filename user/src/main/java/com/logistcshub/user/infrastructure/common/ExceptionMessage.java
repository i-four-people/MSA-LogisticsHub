package com.logistcshub.user.infrastructure.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {

    AUTHORIZATION(HttpStatus.UNAUTHORIZED, "잘못된 인증정보입니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
