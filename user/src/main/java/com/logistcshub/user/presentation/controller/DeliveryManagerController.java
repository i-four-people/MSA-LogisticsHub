package com.logistcshub.user.presentation.controller;

import com.logistcshub.user.application.dtos.DeliveryManagerDto;
import com.logistcshub.user.application.security.UserDetailsImpl;
import com.logistcshub.user.application.service.DeliveryManagerService;
import com.logistcshub.user.infrastructure.common.ApiResponse;
import com.logistcshub.user.infrastructure.common.MessageType;
import com.logistcshub.user.presentation.request.DeliveryManagerCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/delivery-managers")
@RequiredArgsConstructor
public class DeliveryManagerController {

    private final DeliveryManagerService deliveryManagerService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER')")
    public ApiResponse<DeliveryManagerDto> create(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @RequestBody DeliveryManagerCreate createRequest) {

        return ApiResponse.<DeliveryManagerDto>builder()
                .messageType(MessageType.CREATE)
                .data(deliveryManagerService.create(userDetails, createRequest))
                .build();
    }
}
