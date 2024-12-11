package com.logistcshub.user.presentation.controller;

import com.logistcshub.user.application.dtos.MyInfoDto;
import com.logistcshub.user.application.service.UserService;
import com.logistcshub.user.infrastructure.common.ApiResponse;
import com.logistcshub.user.infrastructure.common.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/{id}")
    public ApiResponse<MyInfoDto> getMyInfo(@PathVariable Long id) {
        return ApiResponse.<MyInfoDto>builder()
                .messageType(MessageType.RETRIEVE)
                .data(userService.getMyInfo(id))
                .build();
    }

}
