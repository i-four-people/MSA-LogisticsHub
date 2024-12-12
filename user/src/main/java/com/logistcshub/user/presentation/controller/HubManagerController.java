package com.logistcshub.user.presentation.controller;

import com.logistcshub.user.application.security.UserDetailsImpl;
import com.logistcshub.user.application.service.HubManagerService;
import com.logistcshub.user.infrastructure.common.ApiResponse;
import com.logistcshub.user.infrastructure.common.MessageType;
import com.logistcshub.user.presentation.request.HubManagerRequest;
import com.logistcshub.user.presentation.response.HubManagerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hub-managers")
@RequiredArgsConstructor
public class HubManagerController {

    private final HubManagerService hubManagerService;

    // 허브 매니저 등록
    @PostMapping
    public ApiResponse<HubManagerResponse> createHubManager(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @RequestBody HubManagerRequest hubManagerRequest) {
        return ApiResponse.<HubManagerResponse>builder()
                .messageType(MessageType.CREATE)
                .data(hubManagerService.createHubManager(userDetails, hubManagerRequest))
                .build();
    }
}
