package com.logistics.delivery.application.dto.event;

import lombok.Getter;

@Getter
public enum EventType {
    ORDER_CREATED,
    ORDER_DELETED,
    DELIVERY_CREATED,
    SLACK_CREATED,
}
