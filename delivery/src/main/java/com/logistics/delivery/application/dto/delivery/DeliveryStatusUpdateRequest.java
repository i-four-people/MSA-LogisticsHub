package com.logistics.delivery.application.dto.delivery;

import com.logistics.delivery.domain.model.DeliveryStatus;

public record DeliveryStatusUpdateRequest(
        DeliveryStatus status
) {}
