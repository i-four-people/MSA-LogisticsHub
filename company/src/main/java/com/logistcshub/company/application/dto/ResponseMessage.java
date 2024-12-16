package com.logistcshub.company.application.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    SUCCESS_CREATE_AREA(HttpStatus.OK, "지역 생성에 성공했습니다."),
    SUCCESS_UPDATE_AREA(HttpStatus.OK, "지역 수정에 성공했습니다."),
    SUCCESS_DELETE_AREA(HttpStatus.OK, "지역 삭제에 성공했습니다."),
    SUCCESS_SEARCH_AREA(HttpStatus.OK, "지역 조회에 성공했습니다."),
    SUCCESS_GET_AREA(HttpStatus.OK, "지역 상세 조회에 성공했습니다."),

    SUCCESS_CREATE_HUB(HttpStatus.OK, "허브 생성에 성공했습니다."),
    SUCCESS_UPDATE_HUB(HttpStatus.OK, "허브 수정에 성공했습니다."),
    SUCCESS_DELETE_HUB(HttpStatus.OK, "허브 삭제에 성공했습니다."),
    SUCCESS_GET_HUBS(HttpStatus.OK, "허브 조회에 성공했습니다."),
    SUCCESS_GET_HUB(HttpStatus.OK, "허브 상세 조회에 성공했습니다."),

    SUCCESS_CREATE_HUB_TRANSFER(HttpStatus.OK, "허브 to 허브 생성에 성공했습니다."),
    SUCCESS_UPDATE_HUB_TRANSFER(HttpStatus.OK, "허브 to 허브 수정에 성공했습니다."),
    SUCCESS_DELETE_HUB_TRANSFER(HttpStatus.OK, "허브 to 허브 삭제에 성공했습니다."),
    SUCCESS_GET_HUB_TRANSFER(HttpStatus.OK, "허브 to 허브 조회에 성공했습니다."),



    SUCCESS_SEARCH_HUB_TRANSFERS(HttpStatus.OK, "Hub-Transfer 검색에 성공했습니다.")

    ;
    private final HttpStatus status;
    private final String message;

}

