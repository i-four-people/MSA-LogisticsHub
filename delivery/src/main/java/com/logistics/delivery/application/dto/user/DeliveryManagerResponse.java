package com.logistics.delivery.application.dto.user;

import java.util.UUID;

public record DeliveryManagerResponse(
        UUID id,              // 배송 담당자 ID
        String name,          // 배송 담당자 이름
        String slackId,       // 배송 담당자 Slack ID
        int sequence,         // 순번
        String type           // 배송 담당자 타입 (허브/업체 구분)
) {
}
