package com.logistics.delivery.application.dto.delivery;

import com.logistics.delivery.application.dto.hub.HubResponse;
import com.logistics.delivery.domain.model.DeliveryRoute;
import com.logistics.delivery.domain.model.RouteStatus;

import java.util.UUID;

public record DeliveryRouteResponse(
        UUID id, // 배송 이동 경로 ID
        UUID deliveryId, // 배송 ID
        Long deliveryManagerId, // 배송 담당자 ID
        int sequence, // 배송 경로 상 순번
        UUID startHubId, // 출발 허브 ID
        String startHubName, // 출발 허브명
        UUID endHubId, // 도착 허브 ID,
        String endHubName, // 도착 허브명
        float estimatedDistance, // 경로 상 예상 거리
        float estimatedDuration, // 경로 상 예상 소요 시간
        float actualDistance, // 실제 거리
        float actualDuration, // 실제 소요 시간
        RouteStatus status // 경로 상태
) {

    public static DeliveryRouteResponse from(DeliveryRoute deliveryRoute, HubResponse startHub, HubResponse endHub) {
        return new DeliveryRouteResponse(
                deliveryRoute.getId(),
                deliveryRoute.getDeliveryId(),
                deliveryRoute.getDeliveryManagerId(),
                deliveryRoute.getSequence(),
                deliveryRoute.getStartHubId(),
                startHub.name(),
                deliveryRoute.getEndHubId(),
                endHub.name(),
                deliveryRoute.getEstimatedDistance(),
                deliveryRoute.getEstimatedDuration(),
                deliveryRoute.getActualDistance(),
                deliveryRoute.getActualDuration(),
                deliveryRoute.getStatus()
        );
    }
}
