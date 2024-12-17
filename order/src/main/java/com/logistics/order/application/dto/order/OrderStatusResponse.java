package com.logistics.order.application.dto.order;

import com.logistics.order.domain.model.Order;
import com.logistics.order.domain.model.OrderStatus;

import java.util.UUID;

public record OrderStatusResponse(
        UUID id,                  // 주문 ID
        OrderStatus status
) {

    public static OrderStatusResponse from(Order order) {
        return new OrderStatusResponse(
                order.getId(),
                order.getStatus()
        );
    }
}
