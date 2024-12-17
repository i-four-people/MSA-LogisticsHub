package com.logistics.order.application.dto.event;

import com.logistics.order.application.dto.order.OrderCreateRequest;
import com.logistics.order.domain.model.Order;
import com.logistics.order.presentation.auth.AuthHeaderInfo;

import java.util.UUID;

public record OrderCreateEvent(
        EventType eventType,
        UUID orderId,
        UUID productId,
        UUID supplyCompanyId,
        UUID recipientCompanyId,
        int quantity,
        String requestNote, // 요청 사항
        String deliveryAddress, // 배송지 주소
        String recipientName, // 수령자명
        String recipientSlackId, // 수령자 Slack ID

        Long userId,
        String role
) {

    public static OrderCreateEvent of(Order order, OrderCreateRequest request, AuthHeaderInfo authHeaderInfo) {
        return new OrderCreateEvent(
                EventType.ORDER_CREATED,
                order.getId(),
                order.getProductId(),
                order.getSupplyCompanyId(),
                order.getRecipientCompanyId(),
                order.getQuantity(),
                order.getRequestNotes(),
                request.deliveryAddress(),
                request.recipientName(),
                request.recipientSlackId(),
                authHeaderInfo.userId(),
                authHeaderInfo.role()
        );
    }
}
