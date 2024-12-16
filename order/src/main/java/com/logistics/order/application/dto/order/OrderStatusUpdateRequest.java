package com.logistics.order.application.dto.order;

import com.logistics.order.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record OrderStatusUpdateRequest(
        @NotNull(message = "주문 상태는 필수 입력값입니다.")
        @Pattern(
                regexp = "PENDING|PROCESSING|COMPLETED|CANCELLED",
                message = "주문 상태값은 PENDING, PROCESSING, COMPLETED, CANCELLED 중 하나여야 합니다."
        )
        OrderStatus status
) {
}
