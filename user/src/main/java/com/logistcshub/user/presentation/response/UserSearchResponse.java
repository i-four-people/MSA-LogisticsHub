package com.logistcshub.user.presentation.response;

import java.io.Serializable;
import java.util.List;

public record UserSearchResponse(List<UserDto> userList) implements Serializable {
}
