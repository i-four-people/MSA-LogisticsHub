package com.logistics.order.application.dto.product;

import java.util.UUID;

public record ProductIdsResponse(
        UUID productId,
        String productName,
        int stock,
        UUID companyId
) {
}
