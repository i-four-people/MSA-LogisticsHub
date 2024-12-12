package com.logistics.order.application.dto.event;

import com.logistics.order.domain.model.Order;

import java.util.UUID;

public record OrderDeleteEvent(
        UUID orderId,
        UUID deliveryId
) {
    public static OrderDeleteEvent of(Order order) {
        return new OrderDeleteEvent(
                order.getId(),
                order.getDeliveryId()
        );
    }
}
