package com.logistcshub.user.domain.repository;

import com.logistcshub.user.presentation.response.user.UserSearchResponse;
import com.logistcshub.user.presentation.request.user.UserSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<UserSearchResponse> findAllUser(Pageable pageable, UserSearchRequest userSearchRequest);
}
