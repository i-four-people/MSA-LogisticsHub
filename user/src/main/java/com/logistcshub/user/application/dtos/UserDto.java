package com.logistcshub.user.application.dtos;

import com.logistcshub.user.domain.model.User;
import com.logistcshub.user.domain.model.UserRoleEnum;

import java.time.LocalDateTime;
import java.io.Serializable;

public record UserDto(
        Long userId,
        String username,
        String email,
        String tel,
        String slackId,
        UserRoleEnum role,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy
) implements Serializable {

    public static UserDto of(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getTel(),
                user.getSlackId(),
                user.getRole(),
                user.getCreatedAt(),
                user.getCreatedBy(),
                user.getUpdatedAt(),
                user.getUpdatedBy()
        );
    }
}
