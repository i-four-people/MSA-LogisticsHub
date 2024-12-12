package com.logistics.order.application.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record OrderUpdateRequest(
        @Positive(message = "Quantity must be greater than 0.")
        int quantity,

        String requestNote
) {
}
