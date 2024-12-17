package com.logistcshub.user.presentation.controller;

import com.logistcshub.user.application.service.DeliveryManagerService;
import com.logistcshub.user.common.response.CommonResponse;
import com.logistcshub.user.common.response.SuccessResponse;
import com.logistcshub.user.common.security.UserDetailsImpl;
import com.logistcshub.user.domain.model.deliveryManager.DeliveryManagerType;
import com.logistcshub.user.presentation.request.DeliveryManagerUpdateRequest;
import com.logistcshub.user.presentation.request.deliveryManager.DeliSearchRequest;
import com.logistcshub.user.presentation.request.deliveryManager.DeliveryManagerCreate;
import com.logistcshub.user.presentation.request.deliveryManager.DeliveryManagerUpdate;
import com.logistcshub.user.presentation.response.deliveryManager.DeliSearchResponse;
import com.logistcshub.user.presentation.response.deliveryManager.DeliveryManagerDto;
import com.logistcshub.user.presentation.response.deliveryManager.DeliveryManagerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.logistcshub.user.common.message.SuccessMessage.*;

@RestController
@RequestMapping("/api/delivery-managers")
@RequiredArgsConstructor
public class DeliveryManagerController {

    private final DeliveryManagerService deliveryManagerService;

    // 배송 담당자 등록
    @PostMapping
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER')")
    public ResponseEntity<? extends CommonResponse> create(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           @RequestBody DeliveryManagerCreate createRequest) {

        DeliveryManagerDto output = deliveryManagerService.create(userDetails, createRequest);

        return ResponseEntity.status(SUCCESS_CREATE_DELIVERY_MANAGER.getHttpStatus())
                .body(SuccessResponse.success(SUCCESS_CREATE_DELIVERY_MANAGER.getMessage(),output));
    }

    // 배송 담당자 전체 조회
    @GetMapping
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER')")
    public ResponseEntity<? extends CommonResponse> getAll(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault Pageable pageable,
            DeliSearchRequest deliSearchRequest) {

        Page<DeliSearchResponse> deliveryManagerList =
                deliveryManagerService.getAll(userDetails, pageable, deliSearchRequest);

        return ResponseEntity.status(SUCCESS_GET_ALL_DELIVERY_MANAGER.getHttpStatus())
                .body(SuccessResponse.success(SUCCESS_GET_ALL_DELIVERY_MANAGER.getMessage(), deliveryManagerList));
    }

    // 배송 담당자 상세 조회
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER')")
    public ResponseEntity<? extends CommonResponse> get(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable Long id) {

        DeliveryManagerDto output = deliveryManagerService.get(userDetails, id);

        return ResponseEntity.status(SUCCESS_GET_SINGLE_DELIVERY_MANGER.getHttpStatus())
                .body(SuccessResponse.success(SUCCESS_GET_SINGLE_DELIVERY_MANGER.getMessage(), output));
    }

    // 배송 담당자 수정
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER')")
    public ResponseEntity<? extends CommonResponse> update(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @RequestBody DeliveryManagerUpdate deliveryManagerUpdate,
                                                  @PathVariable Long id) {

        DeliveryManagerDto output = deliveryManagerService.update(userDetails, deliveryManagerUpdate, id);

        return ResponseEntity.status(SUCCESS_UPDATE_DELIVERY_MANAGER.getHttpStatus())
                .body(SuccessResponse.success(SUCCESS_UPDATE_DELIVERY_MANAGER.getMessage(), output));
    }

    // 배송 담당자 삭제
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'HUB_MANAGER')")
    public ResponseEntity<? extends CommonResponse> delete(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @PathVariable Long id ) {

        String output = deliveryManagerService.delete(userDetails, id);

        return ResponseEntity.status(SUCCESS_DELETE_DELIVERY_MANAGER.getHttpStatus())
                .body(SuccessResponse.success(SUCCESS_DELETE_DELIVERY_MANAGER.getMessage(), output));
    }

    // Delivery 호출 Api
    @GetMapping("/api/delivery-managers/available-manager")
    public List<DeliveryManagerResponse> findAvailableManagers(
            @RequestParam(defaultValue = "HUB_PIC") DeliveryManagerType type) {
        return deliveryManagerService.findAvailableManagers(type);
    }

    @PutMapping("/api/delivery-managers/{deliveryManagerId}/hub")
   public void updateHubForManager(@PathVariable("deliveryManagerId") Long deliveryManagerId,
                             @RequestBody DeliveryManagerUpdateRequest request) {
        deliveryManagerService.updateHubForManager(deliveryManagerId, request);
    }
}
