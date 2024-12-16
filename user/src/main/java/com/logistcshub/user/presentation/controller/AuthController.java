package com.logistcshub.user.presentation.controller;

import com.logistcshub.user.application.service.AuthService;
import com.logistcshub.user.common.response.CommonResponse;
import com.logistcshub.user.common.response.SuccessResponse;
import com.logistcshub.user.presentation.request.LoginRequest;
import com.logistcshub.user.presentation.request.SignupRequest;
import com.logistcshub.user.presentation.response.TokenDto;
import com.logistcshub.user.presentation.response.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.logistcshub.user.common.message.SuccessMessage.SUCCESS_LOGIN_USER;
import static com.logistcshub.user.common.message.SuccessMessage.SUCCESS_SIGNUP_USER;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<? extends CommonResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {

        UserDto output = authService.signup(signupRequest);

        return ResponseEntity.status(SUCCESS_SIGNUP_USER.getHttpStatus())
                .body(SuccessResponse.success(SUCCESS_SIGNUP_USER.getMessage(), output));
    }

    @PostMapping("/login")
    public ResponseEntity<? extends CommonResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        TokenDto output = authService.login(loginRequest);

        return ResponseEntity.status(SUCCESS_LOGIN_USER.getHttpStatus())
                .body(SuccessResponse.success(SUCCESS_LOGIN_USER.getMessage(), output));
    }
}
