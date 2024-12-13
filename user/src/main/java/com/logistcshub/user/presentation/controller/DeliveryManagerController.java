package com.logistcshub.user.presentation.controller;

import com.logistcshub.user.presentation.response.DeliveryManagerDto;
import com.logistcshub.user.application.security.UserDetailsImpl;
import com.logistcshub.user.application.service.DeliveryManagerService;
import com.logistcshub.user.infrastructure.common.ApiResponse;
import com.logistcshub.user.infrastructure.common.MessageType;
import com.logistcshub.user.presentation.request.DeliSearchRequest;
import com.logistcshub.user.presentation.request.DeliveryManagerCreate;
import com.logistcshub.user.presentation.request.DeliveryManagerUpdate;
import com.logistcshub.user.presentation.response.DeliSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery-managers")
@RequiredArgsConstructor
public class DeliveryManagerController {

    private final DeliveryManagerService deliveryManagerService;

    // 배송 담당자 등록
    @PostMapping
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER')")
    public ApiResponse<DeliveryManagerDto> create(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @RequestBody DeliveryManagerCreate createRequest) {

        return ApiResponse.<DeliveryManagerDto>builder()
                .messageType(MessageType.CREATE)
                .data(deliveryManagerService.create(userDetails, createRequest))
                .build();
    }

    // 배송 담당자 전체 조회
    @GetMapping
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER')")
    public ApiResponse<Page<DeliSearchResponse>> getAll(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault Pageable pageable,
            DeliSearchRequest deliSearchRequest) {

        Page<DeliSearchResponse> deliveryManagerList = deliveryManagerService.getAll(userDetails, pageable, deliSearchRequest);

        return ApiResponse.<Page<DeliSearchResponse>>builder()
                .messageType(MessageType.RETRIEVE)
                .data(deliveryManagerList)
                .build();
    }

    // 배송 담당자 상세 조회
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER')")
    public ApiResponse<DeliveryManagerDto> get(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable Long id) {
        return ApiResponse.<DeliveryManagerDto>builder()
                .messageType(MessageType.RETRIEVE)
                .data(deliveryManagerService.get(userDetails, id))
                .build();
    }

    // 배송 담당자 수정
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER')")
    public ApiResponse<DeliveryManagerDto> update(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @RequestBody DeliveryManagerUpdate deliveryManagerUpdate,
                                                  @PathVariable Long id) {
        return ApiResponse.<DeliveryManagerDto>builder()
                .messageType(MessageType.UPDATE)
                .data(deliveryManagerService.update(userDetails, deliveryManagerUpdate, id))
                .build();
    }

    // 배송 담당자 삭제
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER')")
    public ApiResponse<String> delete(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @PathVariable Long id ) {
        return ApiResponse.<String>builder()
                .messageType(MessageType.DELETE)
                .data(deliveryManagerService.delete(userDetails, id))
                .build();
    }
}
