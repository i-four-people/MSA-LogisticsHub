package com.logistics.delivery.application.dto.delivery;

import com.logistics.delivery.domain.model.Delivery;

import java.util.UUID;

public record DeliveryDeleteResponse(
        UUID id, // 배송 ID,
        UUID orderId // 주문 ID
) {

    public static DeliveryDeleteResponse from(Delivery delivery) {
        return new DeliveryDeleteResponse(
                delivery.getId(),
                delivery.getOrderId()
        );
    }
}
