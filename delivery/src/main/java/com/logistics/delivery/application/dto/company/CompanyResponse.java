package com.logistics.delivery.application.dto.company;

import java.util.UUID;

public record CompanyResponse(
        UUID id,
        String name,
        UUID hubId,
        String address
) {
}