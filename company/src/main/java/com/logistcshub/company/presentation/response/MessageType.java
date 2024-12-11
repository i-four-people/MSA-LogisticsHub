package com.logistcshub.company.presentation.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
    RETRIEVE("조회되었습니다."),
    CREATE("생성되었습니다."),
    UPDATE("수정되었습니다."),
    DELETE("삭제되었습니다.");

    private final String message;

}
