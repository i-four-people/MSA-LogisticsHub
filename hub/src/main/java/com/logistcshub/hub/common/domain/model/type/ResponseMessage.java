package com.logistcshub.hub.common.domain.model.type;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    SUCCESS_CREATE_AREA(HttpStatus.OK, "지역 생성에 성공했습니다."),
    SUCCESS_UPDATE_AREA(HttpStatus.OK, "지역 수정에 성공했습니다."),
    SUCCESS_DELETE_AREA(HttpStatus.OK, "지역 삭제에 성공했습니다."),
    SUCCESS_SEARCH_AREA(HttpStatus.OK, "지역 조회에 성공했습니다"),

    SUCCESS_CREATE_HUB(HttpStatus.OK, "허브 생성에 성공했습니다."),
    ;
    private final HttpStatus status;
    private final String message;

}

