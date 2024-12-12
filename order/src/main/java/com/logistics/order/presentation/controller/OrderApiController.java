package com.logistics.order.presentation.controller;

import com.logistics.order.application.dto.OrderResponse;
import com.logistics.order.application.dto.PageResponse;
import com.logistics.order.domain.service.OrderService;
import com.logistics.order.presentation.request.SearchParameter;
import com.logistics.order.presentation.response.ApiResponse;
import com.logistics.order.presentation.response.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    @GetMapping("/")
    public ApiResponse<?> getOrders(@ModelAttribute SearchParameter searchParameter) {
        PageResponse<OrderResponse> results = orderService.getOrders(searchParameter);
        return ApiResponse.success(MessageType.RETRIEVE, results);
    }

}
