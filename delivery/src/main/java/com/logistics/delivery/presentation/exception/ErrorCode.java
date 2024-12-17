package com.logistics.delivery.presentation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "E-001", "입력값 검증에 실패했습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E-002", "서버에 오류가 발생했습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E-003", "지원하지 않는 HTTP Method 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E-005", "인증에 실패했습니다."),
    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "D-001", "존재하지 않는 배송 정보입니다."),
    DUPLICATE_DELIVERY(HttpStatus.CONFLICT, "D-002", "해당 주문에 이미 존재하는 배송 정보입니다."),
    DELIVERY_DEPENDENT_ORDER_EXISTS(HttpStatus.BAD_REQUEST, "D-003", "해당 배송에 연관된 주문이 있어 삭제할 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
