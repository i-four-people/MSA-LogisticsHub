package com.logistics.order.application.dto.product;

import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String productName,
        UUID companyId
) {
}
