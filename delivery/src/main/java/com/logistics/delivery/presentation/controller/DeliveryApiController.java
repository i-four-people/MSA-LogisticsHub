package com.logistics.delivery.presentation.controller;

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
}
