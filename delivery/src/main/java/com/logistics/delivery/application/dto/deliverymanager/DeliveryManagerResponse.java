package com.logistics.delivery.application.dto.deliverymanager;

import java.util.UUID;

public record DeliveryManagerResponse(
        Long id,              // 배송 담당자 ID
        String name,          // 배송 담당자 이름
        UUID hubId,           // 배송 담당자의 현재 위치한 hubId
        String slackId,       // 배송 담당자 Slack ID
        String sequence,      // 순번
        String type           // 배송 담당자 타입 (허브/업체 구분)
) {
}
