package com.logistcshub.user.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {

    SUCCESS_CREATE_USER(HttpStatus.CREATED, "유저 생성이 완료되었습니다."),
    SUCCESS_FIND_SINGLE_USER(HttpStatus.OK, "유저 상세 조회가 완료되었습니다."),
    SUCCESS_FIND_ALL_USER(HttpStatus.OK, "유저 목록 조회가 완료되었습니다."),
    SUCCESS_UPDATE_USER(HttpStatus.OK, "유저 업데이트가 완료되었습니다."),
    SUCCESS_DELETE_USER(HttpStatus.OK, "유저 삭제가 완료되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
