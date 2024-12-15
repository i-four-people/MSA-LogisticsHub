package com.logistcshub.company.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    KAKAO_MAP_CLIENT_ERROR(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    KAKAO_MAP_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "카카오 맵 API 서버 에러입니다."),
    KAKAO_MAP_TIME_OUT(HttpStatus.GATEWAY_TIMEOUT, "카카오 맵 API TIME OUT"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR 입니다.");

    private final HttpStatus httpStatus;
    private final String description;
}
