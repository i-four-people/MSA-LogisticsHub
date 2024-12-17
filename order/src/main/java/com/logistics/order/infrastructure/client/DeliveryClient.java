package com.logistics.order.infrastructure.client;

import com.logistics.order.application.dto.delivery.DeliveryResponse;
import com.logistics.order.presentation.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @GetMapping("/api/deliveries/{deliveryId}")
    ApiResponse<DeliveryResponse> findDeliveryById(@PathVariable("deliveryId") UUID deliveryId);

    @GetMapping("/api/deliveries/{deliveryId}/order-status")
    ApiResponse<Boolean> isOrderStatusChangeAllowed(@PathVariable("deliveryId") UUID deliveryId,
                                       String orderStatus);

}
