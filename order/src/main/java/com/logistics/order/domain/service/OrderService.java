package com.logistics.order.domain.service;

import com.logistics.order.application.dto.OrderCreateRequest;
import com.logistics.order.application.dto.OrderResponse;
import com.logistics.order.application.dto.PageResponse;
import com.logistics.order.presentation.request.SearchParameter;

public interface OrderService {

    void createOrder(OrderCreateRequest request);

    PageResponse<OrderResponse> getOrders(SearchParameter searchParameter);

}
