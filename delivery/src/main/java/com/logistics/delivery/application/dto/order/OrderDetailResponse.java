package com.logistics.delivery.application.dto.order;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderDetailResponse(
        UUID id,                  // 주문 ID
        UUID requesterCompanyId,  // 요청 업체 ID
        String requesterCompanyName, // 요청 업체명
        UUID recipientCompanyId,  // 수령 업체 ID
        String recipientCompanyName, // 수령 업체명
        UUID deliveryId,          // 배송 ID
        UUID productId,           // 상품 ID
        String productName,       // 상품명
        int quantity,             // 상품 수량
        int unitPrice,            // 주문 상품 가격
        int totalPrice,           // 주문 총 가격
        String requestNotes,      // 요청 사항
        LocalDateTime createdAt,  // 생성일
        LocalDateTime updatedAt   // 수정일
) {
    public static OrderDetailResponse test(UUID id, UUID deliveryId, String recipientCompanyName, String productName, int quantity, String requestNotes) {
        return new OrderDetailResponse(
                id,
                null,
                null,
                null,
                recipientCompanyName,
                deliveryId,
                null,
                productName,
                quantity,
                0,
                0,
                requestNotes,
                null,
                null
        );
    }
}

