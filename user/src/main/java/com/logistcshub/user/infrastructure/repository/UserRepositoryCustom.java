package com.logistcshub.user.infrastructure.repository;

import com.logistcshub.user.application.dtos.SearchResponse;
import com.logistcshub.user.presentation.request.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<SearchResponse> findAllUser(Pageable pageable, SearchRequest searchRequest);
}
