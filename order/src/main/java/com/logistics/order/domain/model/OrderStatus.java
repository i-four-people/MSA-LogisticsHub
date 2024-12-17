package com.logistics.order.domain.model;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    CANCELED,
}
