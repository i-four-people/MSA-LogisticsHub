package com.logiticshub.product.presentation.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
    RETRIEVE("상품이 조회되었습니다."),
    CREATE("상품이 생성되었습니다."),
    UPDATE("상품이 수정되었습니다."),
    DELETE("상품이 삭제되었습니다."),
    NOT_FOUND("해당하는 상품이 없습니다.");

    private final String message;

}
