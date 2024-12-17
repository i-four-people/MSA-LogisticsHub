package com.logistics.delivery.application.dto.order;

import com.logistics.delivery.application.common.OrderStatus;

public record OrderStatusRequest(
        OrderStatus orderStatus
) {
}
