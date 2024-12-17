package com.logistcshub.user.application.service;

import com.logistcshub.user.domain.model.user.User;
import com.logistcshub.user.domain.model.user.UserRoleEnum;
import com.logistcshub.user.infrastructure.repository.UserRepository;
import com.logistcshub.user.presentation.request.user.UserSearchRequest;
import com.logistcshub.user.presentation.request.user.UserUpdateRequest;
import com.logistcshub.user.presentation.response.user.MyInfoDto;
import com.logistcshub.user.presentation.response.user.UserDto;
import com.logistcshub.user.presentation.response.user.UserSearchResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public MyInfoDto getMyInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("등록하지 않은 유저입니다."));

        return MyInfoDto.of(user);
    }

    @Cacheable(cacheNames = "user_list", key = "#role + ':' + #userSearchRequest.userId + ':' + #userSearchRequest.role + ':' + #userSearchRequest.username")
    @Transactional(readOnly = true)
    public Page<UserSearchResponse> getUserList(UserRoleEnum role, Pageable pageable, UserSearchRequest userSearchRequest) {

        return userRepository.findAllUser(pageable, userSearchRequest);
    }

    @Cacheable(cacheNames = "user_info", key = "#role + ':' + #id")
    public UserDto get(UserRoleEnum role, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("등록하지 않은 유저입니다."));

        return UserDto.of(user);
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
        user.delete(user.getUsername());

        return user.getUsername() + " 회원님 탈퇴 완로 되었습니다.";
    }
}
