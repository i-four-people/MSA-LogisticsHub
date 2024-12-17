package com.logistics.delivery.application.dto.event;

import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerResponse;
import com.logistics.delivery.domain.model.DeliveryRoute;

import java.util.UUID;

public record SlackCreateEvent(
        EventType eventType,
        UUID deliveryId, // 배송 ID
        Long deliveryManagerId, // 배송 담당자 ID
        String deliveryManagerName, // 배송 담당자 이름
        String deliveryManagerSlackId, // 배송 담당자 slack Id
        UUID startHubId, // 출발 허브 ID,
        UUID endHubId // 도착 허브 ID
) {
    public static SlackCreateEvent of(DeliveryRoute deliveryRoute, DeliveryManagerResponse managerResponse) {
        return new SlackCreateEvent(
                EventType.SLACK_CREATED,
                deliveryRoute.getDeliveryId(),
                managerResponse.id(),
                managerResponse.name(),
                managerResponse.slackId(),
                deliveryRoute.getStartHubId(),
                deliveryRoute.getEndHubId()
        );
    }
}
