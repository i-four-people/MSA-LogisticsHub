package com.logistics.order.application.dto.event;

import com.logistics.order.domain.model.Order;

import java.util.UUID;

public record OrderDeleteEvent(
        EventType eventType,
        UUID orderId,
        UUID deliveryId
) {
    public static OrderDeleteEvent of(Order order) {
        return new OrderDeleteEvent(
                EventType.ORDER_DELETED,
                order.getId(),
                order.getDeliveryId()
        );
    }
}
