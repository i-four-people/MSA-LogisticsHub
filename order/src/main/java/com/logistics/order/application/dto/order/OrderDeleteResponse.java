package com.logistics.order.application.dto.order;

import com.logistics.order.domain.model.Order;

import java.util.UUID;

public record OrderDeleteResponse(
        UUID id // 주문 ID
) {

    public static OrderDeleteResponse from(Order order) {
        return new OrderDeleteResponse(
                order.getId()
        );
    }
}
