package com.logistics.order.application.dto.order;

import com.logistics.order.application.dto.company.CompanyResponse;
import com.logistics.order.application.dto.product.ProductResponse;
import com.logistics.order.domain.model.Order;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderDeleteResponse(
        UUID id // 주문 ID
) {

    public static OrderDeleteResponse from(Order order) {
        return new OrderDeleteResponse(
                order.getId()
        );
    }
}
