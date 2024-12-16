package com.logistics.delivery.application.dto.deliverymanager;

import java.util.UUID;

public record DeliveryManagerUpdateRequest(
        UUID hubId
) {
}
