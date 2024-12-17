package com.logistics.delivery.presentation.controller;

import com.logistics.delivery.application.dto.PageResponse;
import com.logistics.delivery.application.dto.SearchParameter;
import com.logistics.delivery.application.dto.delivery.DeliveryDeleteResponse;
import com.logistics.delivery.application.dto.delivery.DeliveryDetailResponse;
import com.logistics.delivery.application.dto.delivery.DeliveryResponse;
import com.logistics.delivery.application.dto.order.OrderStatusRequest;
import com.logistics.delivery.domain.service.DeliveryService;
import com.logistics.delivery.presentation.response.ApiResponse;
import com.logistics.delivery.presentation.response.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryApiController {

    private final DeliveryService deliveryService;

    @GetMapping("/{deliveryId}/order-status")
    public ApiResponse<Boolean> isOrderStatusChangeAllowed(@PathVariable("deliveryId") UUID deliveryId,
                                                           OrderStatusRequest request) {
        boolean result = deliveryService.isOrderStatusChangeAllowed(deliveryId, request);
        return ApiResponse.success(MessageType.RETRIEVE, result);
    }

    // 배송 전체 조회
    @GetMapping("/")
    public ApiResponse<?> getDeliveries(@ModelAttribute SearchParameter searchParameter) {
        PageResponse<DeliveryResponse> results = deliveryService.getDeliveries(searchParameter);
        return ApiResponse.success(MessageType.RETRIEVE, results);
    }

    // 배송 단건 조회
    @GetMapping("/{deliveryId}")
    public ApiResponse<?> getDeliveryById(@PathVariable("deliveryId") UUID deliveryId) {
        DeliveryDetailResponse result = deliveryService.getDeliveryById(deliveryId);
        return ApiResponse.success(MessageType.RETRIEVE, result);
    }

    // 배송 삭제
    @DeleteMapping("/{deliveryId}")
    public ApiResponse<?> deleteDeliveryById(@PathVariable("deliveryId") UUID deliveryId) {
        DeliveryDeleteResponse result = deliveryService.deleteDeliveryById(deliveryId);
        return ApiResponse.success(MessageType.DELETE, result);
    }
}
