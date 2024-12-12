package com.logistics.order.domain.service;

import com.logistics.order.application.dto.order.*;
import com.logistics.order.application.dto.PageResponse;
import com.logistics.order.application.dto.SearchParameter;

import java.util.UUID;

public interface OrderService {

    void createOrder(OrderCreateRequest request);

    PageResponse<OrderResponse> getOrders(SearchParameter searchParameter);

    OrderDetailResponse getOrderById(UUID orderId);

    OrderDetailResponse updateOrderById(UUID orderId, OrderUpdateRequest request);

    OrderDeleteResponse deleteOrderById(UUID orderId);
}
