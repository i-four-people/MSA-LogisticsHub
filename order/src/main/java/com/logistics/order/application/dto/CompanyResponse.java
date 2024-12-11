package com.logistics.order.application.dto;

import java.util.UUID;

public record CompanyResponse(
        UUID companyId,
        String companyName
) {
}
