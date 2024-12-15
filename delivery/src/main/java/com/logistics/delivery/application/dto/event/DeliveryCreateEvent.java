package com.logistics.delivery.application.dto.event;

import com.logistics.delivery.domain.model.Delivery;

import java.util.UUID;

public record DeliveryCreateEvent(
        EventType eventType,
        UUID deliveryId,
        UUID orderId,
        UUID originHubId,
        UUID destinationHubId
) {

    public static DeliveryCreateEvent of(Delivery delivery) {
        return new DeliveryCreateEvent(
                EventType.DELIVERY_CREATED,
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getOriginHubId(),
                delivery.getDestinationHubId()
        );
    }
}
