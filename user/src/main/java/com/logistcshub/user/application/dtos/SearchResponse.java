package com.logistcshub.user.application.dtos;

import java.io.Serializable;
import java.util.List;

public record SearchResponse(List<UserDto> userList) implements Serializable {
}
