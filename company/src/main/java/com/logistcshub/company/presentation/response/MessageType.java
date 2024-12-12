package com.logistcshub.company.presentation.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
    RETRIEVE("업체가 조회되었습니다."),
    CREATE("업체가 생성되었습니다."),
    UPDATE("업체가 수정되었습니다."),
    DELETE("업체가 삭제되었습니다.");

    private final String message;

}
