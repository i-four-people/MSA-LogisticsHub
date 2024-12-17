package com.logistics.delivery.application.dto.delivery;

import com.logistics.delivery.domain.model.RouteStatus;

public record RouteStatusUpdateRequest(
        RouteStatus status,
        Long deliveryManagerId
) {
}
