package com.logistcshub.user.presentation.controller;

import com.logistcshub.user.presentation.response.MyInfoDto;
import com.logistcshub.user.presentation.response.UserSearchResponse;
import com.logistcshub.user.presentation.response.UserDto;
import com.logistcshub.user.application.security.UserDetailsImpl;
import com.logistcshub.user.application.service.UserService;
import com.logistcshub.user.domain.model.UserRoleEnum;
import com.logistcshub.user.infrastructure.common.ApiResponse;
import com.logistcshub.user.infrastructure.common.MessageType;
import com.logistcshub.user.presentation.request.UserSearchRequest;
import com.logistcshub.user.presentation.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 정보 조회
    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'COMPANY_MAMAGER')")
    public ApiResponse<MyInfoDto> getMyInfo(@PathVariable Long id) {
        return ApiResponse.<MyInfoDto>builder()
                .messageType(MessageType.RETRIEVE)
                .data(userService.getMyInfo(id))
                .build();
    }

    // 유저 전체 조회
    @GetMapping
    @PreAuthorize("hasAuthority('MASTER')")
    public ApiResponse<Page<UserSearchResponse>> getUserList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault Pageable pageable,
            UserSearchRequest userSearchRequest
    ) {
        UserRoleEnum loggedUserRole = userDetails.user().getRole();

        Page<UserSearchResponse> searchedUserList = userService.getUserList(loggedUserRole, pageable, userSearchRequest);

        return ApiResponse.<Page<UserSearchResponse>>builder()
                .messageType(MessageType.RETRIEVE)
                .data(searchedUserList)
                .build();
    }

    // 유저 상세 조회
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ApiResponse<UserDto> get(@PathVariable Long id) {
        return ApiResponse.<UserDto>builder()
                .messageType(MessageType.RETRIEVE)
                .data(userService.get(id))
                .build();
    }

    // 유저 권한 수정
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ApiResponse<UserDto> update(@PathVariable Long id, @RequestBody UserUpdateRequest userUpdateRequest) {
        return ApiResponse.<UserDto>builder()
                .messageType(MessageType.UPDATE)
                .data(userService.update(id, userUpdateRequest))
                .build();
    }

    // 유저 탈퇴
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return ApiResponse.<String>builder()
                .messageType(MessageType.DELETE)
                .data(userService.delete(id))
                .build();
    }
}
