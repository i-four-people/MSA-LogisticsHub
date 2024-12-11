package com.logistcshub.user.application.service;

import com.logistcshub.user.application.dtos.TokenDto;
import com.logistcshub.user.application.dtos.UserDto;
import com.logistcshub.user.application.jwt.JwtUtil;
import com.logistcshub.user.domain.model.User;
import com.logistcshub.user.domain.model.UserRoleEnum;
import com.logistcshub.user.domain.repository.UserRepository;
import com.logistcshub.user.presentation.request.LoginRequest;
import com.logistcshub.user.presentation.request.SignupRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${jwt.admin-token}")
    private String ADMIN_TOKEN;

    public UserDto signup(@Valid SignupRequest signupRequest) {
        // 관리자 검증
        UserRoleEnum userRole = UserRoleEnum.valueOf(signupRequest.role());
        if (userRole.equals(UserRoleEnum.MASTER)) {
            if (!signupRequest.adminToken().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 올바르지 않습니다.");
            }
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.password());

        // 유저 객체 생성
        User user = User.create(signupRequest.username(), encodedPassword, signupRequest.email(), signupRequest.tel(), signupRequest.slackId(), userRole);
        // 저장
        userRepository.save(user);

        return UserDto.of(user);
    }

    public TokenDto login(@Valid LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 유저입니다."));
        log.info("유저 검증 완료 #####");

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())
        ) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        log.info("비밀번호 일치 확인 완료 #####");

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(user.getId().toString(), loginRequest.password());
        context.setAuthentication(authentication);
        log.info("컨텍스트 저장 완료 #####");

        // 토큰 생성
        log.info("토큰 생성 시작 #####");
        return jwtUtil.createAccessToken(user.getId(), user.getRole());
    }
}