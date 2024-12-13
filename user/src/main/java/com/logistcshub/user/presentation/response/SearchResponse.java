package com.logistcshub.user.presentation.response;

import com.logistcshub.user.application.dtos.UserDto;

import java.io.Serializable;
import java.util.List;

public record SearchResponse(List<UserDto> userList) implements Serializable {
}
