package com.logistics.delivery.application.dto.company;

import java.util.UUID;

public record CompanyResponse(
        UUID companyId,
        String companyName,
        UUID hubId
) {
}