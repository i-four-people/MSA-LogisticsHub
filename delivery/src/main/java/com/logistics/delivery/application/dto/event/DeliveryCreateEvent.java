package com.logistics.delivery.application.dto.event;

import com.logistics.delivery.domain.model.Delivery;
import com.logistics.delivery.presentation.auth.AuthHeaderInfo;

import java.util.UUID;

public record DeliveryCreateEvent(
        EventType eventType,
        UUID deliveryId,
        UUID orderId,
        UUID originHubId,
        UUID destinationHubId,

        Long userId,
        String role
) {

    public static DeliveryCreateEvent of(Delivery delivery, AuthHeaderInfo authHeaderInfo) {
        return new DeliveryCreateEvent(
                EventType.DELIVERY_CREATED,
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getOriginHubId(),
                delivery.getDestinationHubId(),
                authHeaderInfo.userId(),
                authHeaderInfo.role()
        );
    }
}
