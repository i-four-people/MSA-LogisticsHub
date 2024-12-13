package com.logistcshub.user.domain.repository;

import com.logistcshub.user.infrastructure.common.SearchResponse;
import com.logistcshub.user.presentation.request.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<SearchResponse> findAllUser(Pageable pageable, SearchRequest searchRequest);
}
