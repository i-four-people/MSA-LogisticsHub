package com.logistics.delivery.application.dto.order;

import java.util.UUID;

public record OrderResponse(
        UUID orderId
) {
}
