package com.logistics.order.application.dto.delivery;

import java.util.UUID;

public record DeliveryResponse(
        UUID deliveryId,
        String status
) {
}
