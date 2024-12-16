package com.logistics.delivery.application.dto.event.consume;

import java.util.UUID;

public record OrderCreateConsume(
        UUID orderId,
        UUID productId,
        UUID supplyCompanyId,
        UUID recipientCompanyId,
        int quantity,
        String requestNote, // 요청 사항
        String deliveryAddress, // 배송지 주소
        String recipientName, // 수령자명
        String recipientSlackId // 수령자 Slack ID
) {
}
