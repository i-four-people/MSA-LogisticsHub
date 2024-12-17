package com.logistics.delivery.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {
    PENDING("배송 대기 중"),
    IN_TRANSIT("배송 중"),
    AT_HUB("목적지 허브 도착"),
    OUT_FOR_DELIVERY("업체로 배송 중"),
    DELIVERED("배송 완료"),
    CANCELLED("배송 취소");

    private final String description;
}
