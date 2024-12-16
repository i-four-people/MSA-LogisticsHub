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

    // 배송
    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "D-001", "존재하지 않는 배송 정보입니다."),
    DUPLICATE_DELIVERY(HttpStatus.CONFLICT, "D-002", "해당 주문에 이미 존재하는 배송 정보입니다."),
    DELIVERY_DEPENDENT_ORDER_EXISTS(HttpStatus.BAD_REQUEST, "D-003", "해당 배송에 연관된 주문이 있어 삭제할 수 없습니다."),
    DELIVERY_ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "D-004", "존재하지 않는 배송 이동 경로 정보입니다."),
    INVALID_DELIVERY_STATUS_CHANGE(HttpStatus.BAD_REQUEST, "D-005", "배송 상태를 변경할 수 없습니다."),

    // 배송 경로
    INVALID_STATUS_CHANGE_FROM_ASSIGNED(HttpStatus.BAD_REQUEST, "R-001", "ASSIGNED 상태에서는 IN_TRANSIT 상태로만 변경 가능합니다."),
    INVALID_STATUS_CHANGE_FROM_IN_TRANSIT(HttpStatus.BAD_REQUEST, "R-002", "IN_TRANSIT 상태에서는 AT_HUB 상태로만 변경 가능합니다."),
    INVALID_STATUS_CHANGE_FROM_AT_HUB(HttpStatus.BAD_REQUEST, "R-003", "AT_HUB 상태에서는 더 이상 상태를 변경할 수 없습니다."),
    MANAGER_NOT_ASSIGNED_TO_ROUTE(HttpStatus.FORBIDDEN, "R-004", "배송 경로에 해당 담당자가 배정되어 있지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
