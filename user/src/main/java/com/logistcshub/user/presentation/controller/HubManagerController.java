package com.logistcshub.user.presentation.controller;

import com.logistcshub.user.application.service.HubManagerService;
import com.logistcshub.user.common.response.CommonResponse;
import com.logistcshub.user.common.response.SuccessResponse;
import com.logistcshub.user.common.security.UserDetailsImpl;
import com.logistcshub.user.presentation.request.HubManagerRequest;
import com.logistcshub.user.presentation.response.HubManagerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.logistcshub.user.common.message.SuccessMessage.SUCCESS_CREATE_HUB_MANAGER;

@RestController
@RequestMapping("/api/hub-managers")
@RequiredArgsConstructor
public class HubManagerController {

    private final HubManagerService hubManagerService;

    // 허브 매니저 등록
    @PostMapping
    public ResponseEntity<? extends CommonResponse> createHubManager(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                     @RequestBody HubManagerRequest hubManagerRequest) {

        HubManagerResponse output = hubManagerService.createHubManager(userDetails, hubManagerRequest);

        return ResponseEntity.status(SUCCESS_CREATE_HUB_MANAGER.getHttpStatus())
                .body(SuccessResponse.success(SUCCESS_CREATE_HUB_MANAGER.getMessage(),output));
    }
}
