package com.logistics.order.application.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record OrderCreateRequest(
        @NotNull(message = "RecipientCompany ID cannot be null.")
        UUID recipientCompanyId,    // 수령 업체 ID

        @NotNull(message = "Product ID cannot be null.")
        UUID productId,

        @Positive(message = "Quantity must be greater than 0.")
        int quantity,

        @Positive(message = "Price must be greater than 0.")
        int price,

        String requestNote, // 요청 사항

        @NotBlank(message = "Delivery address cannot be blank.")
        String deliveryAddress, // 배송지 주소

        @NotBlank(message = "Recipient name cannot be blank.")
        String recipientName, // 수령자명

        @NotBlank(message = "Recipient Slack ID cannot be blank.")
        String recipientSlackId // 수령자 Slack ID
) {
}
