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

    KAKAO_MAP_CLIENT_ERROR(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    KAKAO_MAP_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "카카오 맵 API 서버 에러입니다."),
    KAKAO_MAP_TIME_OUT(HttpStatus.GATEWAY_TIMEOUT, "카카오 맵 API TIME OUT"),

    KAKAO_ROAD_CLIENT_ERROR(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    KAKAO_ROAD_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "카카오 길찾기 API 서버 에러입니다."),
    KAKAO_ROAD_TIME_OUT(HttpStatus.GATEWAY_TIMEOUT, "카카오 길찾기 API TIME OUT"),
    KAKAO_ROAD_NOT_FOUND(HttpStatus.NOT_FOUND, "카카오 길찾기에 실패했습니다."),

    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 허브를 찾을 수 없습니다."),
    ALREADY_EXISTS_HUB(HttpStatus.BAD_REQUEST, "이미 존재하는 허브입니다."),
    ALREADY_EXISTS_HUB_TRANSFER(HttpStatus.BAD_REQUEST, "이미 존재하는 경로입니다."),

    HUB_TRANSFER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 hub transfer를 찾을 수 없습니다."),


    ;

    private final HttpStatus httpStatus;
    private final String description;
}
