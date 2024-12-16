package com.logistics.delivery.application.common;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    CANCELED,
}
