package com.logistics.delivery.application.dto.event.consume;

import com.logistics.delivery.application.dto.event.EventType;

import java.util.UUID;

public record SlackCreateConsume(
        EventType eventType,
        UUID deliveryId, // 배송 ID
        Long deliveryManagerId, // 배송 담당자 ID
        String deliveryManagerName, // 배송 담당자 이름
        String deliveryManagerSlackId, // 배송 담당자 slack Id,
        UUID startHubId, // 출발 허브 ID,
        UUID endHubId // 도착 허브 ID
) {
}
