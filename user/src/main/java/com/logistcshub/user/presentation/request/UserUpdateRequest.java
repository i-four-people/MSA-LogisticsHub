package com.logistcshub.user.presentation.request;

import com.logistcshub.user.domain.model.UserRoleEnum;

public record UserUpdateRequest(UserRoleEnum role) {
}
