package com.logistcshub.user.presentation.controller;

import com.logistcshub.user.application.dtos.MyInfoDto;
import com.logistcshub.user.application.dtos.SearchResponse;
import com.logistcshub.user.application.dtos.UserDto;
import com.logistcshub.user.application.security.UserDetailsImpl;
import com.logistcshub.user.application.service.UserService;
import com.logistcshub.user.domain.model.UserRoleEnum;
import com.logistcshub.user.infrastructure.common.ApiResponse;
import com.logistcshub.user.infrastructure.common.MessageType;
import com.logistcshub.user.infrastructure.common.PageResponse;
import com.logistcshub.user.infrastructure.common.SearchParameter;
import com.logistcshub.user.presentation.request.SearchRequest;
import com.logistcshub.user.presentation.request.UserUpdateRequest;
import io.micrometer.core.instrument.search.Search;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/users")
    public ApiResponse<Page<SearchResponse>> getUserList(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault Pageable pageable,
            SearchRequest searchRequest
    ) {
        UserRoleEnum loggedUserRole = userDetails.user().getRole();

        Page<SearchResponse> searchedUserList = userService.getUserList(loggedUserRole, pageable, searchRequest);

        return ApiResponse.<Page<SearchResponse>>builder()
                .messageType(MessageType.RETRIEVE)
                .data(searchedUserList)
                .build();
    }

    @PatchMapping("/user/{id}")
    public ApiResponse<UserDto> update(@PathVariable Long id, @RequestBody UserUpdateRequest userUpdateRequest) {
        return ApiResponse.<UserDto>builder()
                .messageType(MessageType.UPDATE)
                .data(userService.update(id, userUpdateRequest))
                .build();
    }

    @DeleteMapping("/user/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return ApiResponse.<String>builder()
                .messageType(MessageType.DELETE)
                .data(userService.delete(id))
                .build();
    }

}
