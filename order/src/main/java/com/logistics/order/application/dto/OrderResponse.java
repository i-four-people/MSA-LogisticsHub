package com.logistics.order.application.dto;

import com.logistics.order.domain.model.Order;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,                  // 주문 ID
        UUID requesterCompanyId,  // 요청 업체 ID
        UUID recipientCompanyId,  // 수령 업체 ID
        UUID productId,           // 상품 ID
        int quantity,             // 상품 수량
        int unitPrice,            // 주문 상품 가격
        int totalPrice,           // 주문 총 가격
        String requestNotes,      // 요청 사항
        LocalDateTime createdAt,  // 생성일
        LocalDateTime updatedAt   // 수정일
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getRequesterCompanyId(),
                order.getRecipientCompanyId(),
                order.getProductId(),
                order.getQuantity(),
                order.getPrice().intValue(),
                order.calculateTotalPrice(),
                order.getRequestNotes(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
