package com.logistics.order.application.dto.event;

import com.logistics.order.domain.model.Order;

import java.util.UUID;

public record OrderCreateEvent(
        UUID orderId,
        UUID productId,
        UUID supplyCompanyId,
        UUID recipientCompanyId,
        int quantity
) {
    public static OrderCreateEvent of(Order order) {
        return new OrderCreateEvent(
                order.getId(),
                order.getProductId(),
                order.getSupplyCompanyId(),
                order.getRecipientCompanyId(),
                order.getQuantity()
        );
    }
}
