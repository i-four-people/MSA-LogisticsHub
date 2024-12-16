package com.logistcshub.user.presentation.controller;

import com.logistcshub.user.application.service.UserService;
import com.logistcshub.user.common.response.CommonResponse;
import com.logistcshub.user.common.response.SuccessResponse;
import com.logistcshub.user.common.security.UserDetailsImpl;
import com.logistcshub.user.domain.model.user.UserRoleEnum;
import com.logistcshub.user.presentation.request.user.UserSearchRequest;
import com.logistcshub.user.presentation.request.user.UserUpdateRequest;
import com.logistcshub.user.presentation.response.user.MyInfoDto;
import com.logistcshub.user.presentation.response.user.UserDto;
import com.logistcshub.user.presentation.response.user.UserSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.logistcshub.user.common.message.SuccessMessage.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 정보 조회
    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'COMPANY_MAMAGER')")
    public ResponseEntity<? extends CommonResponse> getMyInfo(@PathVariable Long id) {

        MyInfoDto output = userService.getMyInfo(id);

        return ResponseEntity.status(SUCCESS_GET_SINGLE_USER.getHttpStatus())
                .body(SuccessResponse.success(SUCCESS_GET_SINGLE_USER.getMessage(), output));
    }

    // 유저 전체 조회
    @GetMapping
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<? extends CommonResponse> getUserList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault Pageable pageable,
            UserSearchRequest userSearchRequest
    ) {
        UserRoleEnum loggedUserRole = userDetails.user().getRole();

        Page<UserSearchResponse> searchedUserList =
                userService.getUserList(loggedUserRole, pageable, userSearchRequest);

        return ResponseEntity.status(SUCCESS_GET_ALL_USER.getHttpStatus())
                .body(SuccessResponse.success(SUCCESS_GET_ALL_USER.getMessage(), searchedUserList));
    }

    // 유저 상세 조회
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<? extends CommonResponse> get(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @PathVariable Long id) {
        UserRoleEnum loggedUserRole = userDetails.user().getRole();

        UserDto output = userService.get(loggedUserRole, id);

        return ResponseEntity.status(SUCCESS_GET_SINGLE_USER.getHttpStatus())
                .body(SuccessResponse.success(SUCCESS_GET_SINGLE_USER.getMessage(), output));
    }

    // 유저 권한 수정
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<? extends CommonResponse> update(@PathVariable Long id,
                                                           @RequestBody UserUpdateRequest userUpdateRequest) {

        UserDto output = userService.update(id, userUpdateRequest);

        return ResponseEntity.status(SUCCESS_UPDATE_USER.getHttpStatus())
                .body(SuccessResponse.success(SUCCESS_UPDATE_USER.getMessage(), output));
    }

    // 유저 탈퇴
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MASTER')")
    public ResponseEntity<? extends CommonResponse> delete(@PathVariable Long id) {

        String output = userService.delete(id);

        return ResponseEntity.status(SUCCESS_DELETE_USER.getHttpStatus())
                .body(SuccessResponse.success(SUCCESS_DELETE_USER.getMessage(), output));
    }
}
