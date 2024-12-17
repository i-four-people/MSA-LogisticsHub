package com.logistics.delivery.infrastructure.client;

import com.logistics.delivery.application.dto.order.OrderResponse;
import com.logistics.delivery.application.dto.order.OrderDetailResponse;
import com.logistics.delivery.presentation.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/api/orders/{orderId}")
    ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable("orderId") UUID orderId);

    @GetMapping("/api/orders/{id}")
    ResponseEntity<ApiResponse<OrderDetailResponse>> orderDetails(@PathVariable("id") UUID id);
}
