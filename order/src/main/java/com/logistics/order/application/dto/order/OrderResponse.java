package com.logistics.order.application.dto.order;

import com.logistics.order.application.dto.company.CompanyResponse;
import com.logistics.order.application.dto.product.ProductIdsResponse;
import com.logistics.order.application.dto.product.ProductResponse;
import com.logistics.order.domain.model.Order;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,                  // 주문 ID
        UUID deliveryId,          // 배송 ID
        UUID requesterCompanyId,  // 요청 업체 ID
        String requesterCompanyName, // 요청 업체명
        UUID recipientCompanyId,  // 수령 업체 ID
        String recipientCompanyName, // 수령 업체명
        UUID productId,           // 상품 ID
        String productName,       // 상품명
        int quantity,             // 상품 수량
        int unitPrice,            // 주문 상품 가격
        int totalPrice,           // 주문 총 가격
        String requestNotes,      // 요청 사항
        LocalDateTime createdAt,  // 생성일
        LocalDateTime updatedAt   // 수정일
) {

    public static OrderResponse from(Order order, CompanyResponse requesterCompany, CompanyResponse recipientCompany, ProductIdsResponse product) {
        return new OrderResponse(
                order.getId(),
                order.getDeliveryId(),
                order.getSupplyCompanyId(),
                requesterCompany.companyName(),
                order.getRecipientCompanyId(),
                recipientCompany.companyName(),
                order.getProductId(),
                product.productName(),
                order.getQuantity(),
                order.getPrice().intValue(),
                order.calculateTotalPrice(),
                order.getRequestNotes(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
