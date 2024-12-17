package com.logistcshub.user.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {

    // User
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호 입니다."),
    INVALID_ADMIN_TOKEN(HttpStatus.FORBIDDEN, "관리자 암호가 올바르지 않습니다."),

    AUTHORIZATION(HttpStatus.UNAUTHORIZED, "잘못된 인증정보입니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"),

    // HubManager
    INVALID_HUB_MANAGER(HttpStatus.FORBIDDEN, "해당 유저의 권한이 허브 매니저가 아닙니다."),
    HUB_MANAGER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "매니저님의 담당 허브가 아닙니다."),

    // DeliveryManager,
    INVALID_DELIVERY_MANAGER(HttpStatus.FORBIDDEN, "배송 담당자가 아닙니다."),
    HUB_NOT_ASSIGNED(HttpStatus.BAD_REQUEST, "배송 담당자는 담당 허브를 지정해주어야 합니다."),
    ACCESS_DENIED_OWN_INFO(HttpStatus.FORBIDDEN, "본인 정보만 확인 가능합니다."),

    // Hub
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 허브입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
