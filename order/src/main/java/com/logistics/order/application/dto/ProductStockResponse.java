package com.logistics.order.application.dto;

import java.util.UUID;

public record ProductStockResponse(
        UUID productId,
        int availableStock
) {
}
