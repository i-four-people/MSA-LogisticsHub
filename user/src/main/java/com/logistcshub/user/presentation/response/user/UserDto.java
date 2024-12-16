package com.logistcshub.user.presentation.response.user;

import com.logistcshub.user.domain.model.user.User;
import com.logistcshub.user.domain.model.user.UserRoleEnum;
import com.querydsl.core.annotations.QueryProjection;

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

    @QueryProjection
    public UserDto(Long userId, String username, String email, String tel, String slackId, UserRoleEnum role,
                   LocalDateTime createdAt, String createdBy, LocalDateTime updatedAt, String updatedBy) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.tel = tel;
        this.slackId = slackId;
        this.role = role;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }
}
