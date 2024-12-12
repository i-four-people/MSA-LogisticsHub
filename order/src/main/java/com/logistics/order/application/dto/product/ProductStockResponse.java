package com.logistics.order.application.dto.product;

import java.util.UUID;

public record ProductStockResponse(
        UUID productId,
        int availableStock
) {
}
