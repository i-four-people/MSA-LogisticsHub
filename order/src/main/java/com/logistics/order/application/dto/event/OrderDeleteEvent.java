package com.logistics.order.application.dto.event;

import com.logistics.order.domain.model.Order;
import com.logistics.order.presentation.auth.AuthHeaderInfo;

import java.util.UUID;

public record OrderDeleteEvent(
        EventType eventType,
        UUID orderId,
        UUID deliveryId,

        Long userId,
        String role
) {
    public static OrderDeleteEvent of(Order order, AuthHeaderInfo authHeaderInfo) {
        return new OrderDeleteEvent(
                EventType.ORDER_DELETED,
                order.getId(),
                order.getDeliveryId(),
                authHeaderInfo.userId(),
                authHeaderInfo.role()
        );
    }
}
