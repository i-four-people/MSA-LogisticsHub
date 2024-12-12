package com.logistics.order.application.dto.company;

import java.util.UUID;

public record CompanyResponse(
        UUID companyId,
        String companyName
) {
}
