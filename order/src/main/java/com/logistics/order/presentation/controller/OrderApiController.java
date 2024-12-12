package com.logistics.order.presentation.controller;

import com.logistics.order.application.dto.order.*;
import com.logistics.order.application.dto.PageResponse;
import com.logistics.order.domain.service.OrderService;
import com.logistics.order.application.dto.SearchParameter;
import com.logistics.order.presentation.response.ApiResponse;
import com.logistics.order.presentation.response.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping("/")
    public ApiResponse<?> createOrder(@RequestBody OrderCreateRequest request) {
        orderService.createOrder(request);
        return ApiResponse.success(MessageType.CREATE);
    }

    // 주문 전체 조회
    @GetMapping("/")
    public ApiResponse<?> getOrders(@ModelAttribute SearchParameter searchParameter) {
        PageResponse<OrderResponse> results = orderService.getOrders(searchParameter);
        return ApiResponse.success(MessageType.RETRIEVE, results);
    }

    // 주문 단건 조회
    @GetMapping("/{orderId}")
    public ApiResponse<?> getOrderById(@PathVariable("orderId") UUID orderId) {
        OrderDetailResponse result = orderService.getOrderById(orderId);
        return ApiResponse.success(MessageType.RETRIEVE, result);
    }

    // 주문 수정
    @PutMapping("/{orderId}")
    public ApiResponse<?> updateOrderById(@PathVariable("orderId") UUID orderId,
                                          @RequestBody OrderUpdateRequest request) {
        OrderDetailResponse result = orderService.updateOrderById(orderId, request);
        return ApiResponse.success(MessageType.UPDATE, result);
    }

    // 주문 삭제
    @DeleteMapping("/{orderId}")
    public ApiResponse<?> deleteOrderById(@PathVariable("orderId") UUID orderId) {
        OrderDeleteResponse result = orderService.deleteOrderById(orderId);
        return ApiResponse.success(MessageType.DELETE, result);
    }

}
