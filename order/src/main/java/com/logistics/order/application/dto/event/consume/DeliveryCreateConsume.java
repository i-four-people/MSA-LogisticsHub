package com.logistics.order.application.dto.event.consume;

import com.logistics.order.application.dto.event.EventType;

import java.util.UUID;

public record DeliveryCreateConsume(
        EventType eventType,
        UUID deliveryId,
        UUID orderId,
        UUID originHubId,
        UUID destinationHubId,

        Long userId,
        String role
) {
}
