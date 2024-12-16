package com.logistcshub.user.presentation.response;

import com.logistcshub.user.domain.model.user.User;
import com.logistcshub.user.domain.model.user.UserRoleEnum;

import java.io.Serializable;

public record MyInfoDto(String username, String email, String tel, String slackId, UserRoleEnum role)
        implements Serializable {

    public static MyInfoDto of(User user) {
        return new MyInfoDto(
                user.getUsername(),
                user.getEmail(),
                user.getTel(),
                user.getSlackId(),
                user.getRole()
        );
    }
}
