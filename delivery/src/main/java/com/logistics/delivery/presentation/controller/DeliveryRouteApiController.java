package com.logistics.delivery.presentation.controller;

import com.logistics.delivery.application.dto.delivery.DeliveryRouteResponse;
import com.logistics.delivery.application.dto.delivery.RouteStatusUpdateRequest;
import com.logistics.delivery.domain.service.DeliveryRouteService;
import com.logistics.delivery.presentation.response.ApiResponse;
import com.logistics.delivery.presentation.response.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-routes")
@RequiredArgsConstructor
public class DeliveryRouteApiController {

    private final DeliveryRouteService deliveryRouteService;

    // 배송 이동 경로 상태 업데이트
    @PatchMapping("/{routeId}/status")
    public ApiResponse<?> updateRouteStatus(@PathVariable("routeId") UUID routeId,
                                            @RequestBody RouteStatusUpdateRequest request) {
        DeliveryRouteResponse result = deliveryRouteService.updateRouteStatus(routeId, request);
        return ApiResponse.success(MessageType.UPDATE, result);
    }
}
