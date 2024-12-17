package com.logistics.delivery.application.dto.event.consume;

import com.logistics.delivery.application.dto.event.EventType;

import java.util.UUID;

public record OrderDeleteConsume(
        EventType eventType,
        UUID orderId,
        UUID deliveryId,

        Long userId,
        String role
) {
}
