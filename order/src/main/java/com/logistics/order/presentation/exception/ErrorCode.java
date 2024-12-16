package com.logistics.order.presentation.exception;

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
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E-004", "요청한 리소스를 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E-005", "인증에 실패했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "E-006", "접근 권한이 없습니다."),
    OUT_OF_STOCK(HttpStatus.CONFLICT, "P-001", "상품 재고가 부족합니다."),
    INVALID_STATUS_CHANGE(HttpStatus.BAD_REQUEST, "D-001", "배송 상태로 인해 주문 상태를 변경할 수 없습니다."),

    // 주문
    ORDER_ALREADY_COMPLETED(HttpStatus.CONFLICT, "O-001", "이미 완료된 주문입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
