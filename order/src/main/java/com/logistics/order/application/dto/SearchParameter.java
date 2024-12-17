package com.logistics.order.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class SearchParameter {
    private int page = 1; // 페이지 번호
    private int limit = 10; // 한 페이지에 보여지는 데이터 수
    private String searchValue; // 검색 키워드
    private String searchType; // 검색 유형
    private String orderBy = "createdAt"; // 정렬에 사용되는 필드
    private Sort.Direction sort = Sort.Direction.DESC; // 정렬 순서 (ASC | DESC)

    // 페이지 객체 생성
    public Pageable getPageable() {
        return PageRequest.of(page - 1, limit, sort, orderBy);
    }

}
