package com.logistics.delivery.application.dto.hub;

import java.util.UUID;

public record HubResponse(
        UUID id,
        String name
) {
}
