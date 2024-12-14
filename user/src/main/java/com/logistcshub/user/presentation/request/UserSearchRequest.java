package com.logistcshub.user.presentation.request;

import com.logistcshub.user.domain.model.UserRoleEnum;

public record UserSearchRequest(Long userId, UserRoleEnum role, String username, String email) {
}
