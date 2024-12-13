package com.logistcshub.user.application.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.logistcshub.user.application.client.HubClient;
import com.logistcshub.user.application.dtos.HubDto;
import com.logistcshub.user.application.dtos.MyInfoDto;
import com.logistcshub.user.application.security.UserDetailsImpl;
import com.logistcshub.user.domain.model.HubManager;
import com.logistcshub.user.domain.repository.HubManagerRepository;
import com.logistcshub.user.infrastructure.common.SearchResponse;
import com.logistcshub.user.application.dtos.UserDto;
import com.logistcshub.user.domain.model.User;
import com.logistcshub.user.domain.model.UserRoleEnum;
import com.logistcshub.user.domain.repository.UserRepository;
import com.logistcshub.user.presentation.request.HubManagerRequest;
import com.logistcshub.user.presentation.request.SearchRequest;
import com.logistcshub.user.presentation.request.UserUpdateRequest;
import com.logistcshub.user.presentation.response.HubManagerResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public MyInfoDto getMyInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("등록하지 않은 유저입니다."));

        return MyInfoDto.of(user);
    }

    @Transactional(readOnly = true)
    public Page<SearchResponse> getUserList(UserRoleEnum role, Pageable pageable, SearchRequest searchRequest) {

        return userRepository.findAllUser(pageable, searchRequest);
    }

    @Transactional
    public UserDto update(Long id, UserUpdateRequest userUpdateRequest) {
        User user =  userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("등록하지 않은 유저입니다."));

        user.updateUserRole(UserRoleEnum.valueOf(userUpdateRequest.role()));
        return UserDto.of(user);
    }

    @Transactional
    public String delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("등록하지 않은 유저입니다."));

        // 논리적 삭제
        user.delete(user.getEmail());

        return user.getUsername() + " 회원님 탈퇴 완로 되었습니다.";
    }

    public UserDto get(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("등록하지 않은 유저입니다."));

        return UserDto.of(user);
    }

}
