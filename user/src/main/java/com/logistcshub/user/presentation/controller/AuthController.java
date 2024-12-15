package com.logistcshub.user.presentation.controller;

import com.logistcshub.user.presentation.response.TokenDto;
import com.logistcshub.user.presentation.response.UserDto;
import com.logistcshub.user.application.service.AuthService;
import com.logistcshub.user.common.response.ApiResponse;
import com.logistcshub.user.common.message.MessageType;
import com.logistcshub.user.presentation.request.LoginRequest;
import com.logistcshub.user.presentation.request.SignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<UserDto> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ApiResponse.<UserDto>builder()
                .messageType(MessageType.CREATE)
                .data(authService.signup(signupRequest))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<TokenDto> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ApiResponse.<TokenDto>builder()
                .messageType(MessageType.CREATE)
                .data(authService.login(loginRequest))
                .build();
    }
}
