package com.logistcshub.hub.common.exception.application.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR 입니다."),

    STATE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 시/도를 찾을 수 없습니다"),
    CITY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 시/군/구를 찾을 수 없습니다"),
    AREA_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 지역을 찾을 수 없습니다"),

    ;

    private final HttpStatus httpStatus;
    private final String description;
}
