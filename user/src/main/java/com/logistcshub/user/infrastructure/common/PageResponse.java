package com.logistcshub.user.infrastructure.common;

import org.springframework.data.domain.Page;
import java.util.List;

public record PageResponse<T>(
        List<T> items,          // 현재 페이지의 데이터 리스트
        int pageNumber,         // 현재 페이지 번호
        int pageSize,           // 현재 페이지 사이즈
        long totalElements,     // 전체 데이터 개수
        int totalPages,         // 전체 페이지 수
        boolean first,          // 첫 번째 페이지 여부
        boolean last            // 마지막 페이지 여부
) {

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber() + 1, // 페이지 번호를 1부터 시작하기 위함
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}