package com.logistics.order.application.dto;

import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String productName
) {
}
