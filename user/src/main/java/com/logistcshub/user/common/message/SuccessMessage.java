package com.logistcshub.user.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {

    // Auth
    SUCCESS_SIGNUP_USER(HttpStatus.CREATED, "유저 등록이 완료되었습니다."),
    SUCCESS_LOGIN_USER(HttpStatus.OK, "로그인에 성공하였습니다."),

    // User
    SUCCESS_GET_SINGLE_USER(HttpStatus.OK, "유저 상세 조회가 완료되었습니다."),
    SUCCESS_GET_ALL_USER(HttpStatus.OK, "유저 목록 조회가 완료되었습니다."),
    SUCCESS_UPDATE_USER(HttpStatus.OK, "유저 업데이트가 완료되었습니다."),
    SUCCESS_DELETE_USER(HttpStatus.OK, "유저 삭제가 완료되었습니다."),

    // HubManager
    SUCCESS_CREATE_HUB_MANAGER(HttpStatus.CREATED, "허브 매니저 등록이 완료되었습니다."),

    // DeliveryManager
    SUCCESS_CREATE_DELIVERY_MANAGER(HttpStatus.CREATED, "배송 담당자 등록이 완료되었습니다."),
    SUCCESS_GET_SINGLE_DELIVERY_MANGER(HttpStatus.OK, "배송 담당자 상세 조회가 완료되었습니다."),
    SUCCESS_GET_ALL_DELIVERY_MANAGER(HttpStatus.OK, "배송 담당자 전체 조회가 완료되었습니다."),
    SUCCESS_UPDATE_DELIVERY_MANAGER(HttpStatus.OK, "배송 담당자 수정이 완료되었습니다."),
    SUCCESS_DELETE_DELIVERY_MANAGER(HttpStatus.OK, "배송 담당자 삭제가 완료되었습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
