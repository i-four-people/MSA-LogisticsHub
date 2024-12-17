package com.logistics.order.application.dto.product;

import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        int stock,
        UUID companyId
) {
}
