package com.logistics.delivery.application.dto.delivery;

import com.logistics.delivery.application.dto.hub.HubResponse;
import com.logistics.delivery.domain.model.Delivery;
import com.logistics.delivery.domain.model.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryDetailResponse(
        UUID id, // 배송 ID,
        UUID orderId, // 주문 ID
        DeliveryStatus status, // 배송 상태
        String cancellationReason, // 취소 사유
        UUID originHubId, // 출발 허브 ID
        String originHubName, // 출발 허브명
        UUID destinationHubId, // 도착 허브 ID
        String destinationHubName, // 도착 허브명
        String deliveryAddress, // 배송지 주소
        String recipientName, // 수령자명
        String recipientSlackId, // 수령자 slack Id
        Long companyDeliveryManagerId, // 업체 배송 담당자 ID
        LocalDateTime createdAt,  // 생성일
        LocalDateTime updatedAt,   // 수정일
        List<DeliveryRouteResponse> routes
) {

    public static DeliveryDetailResponse from(Delivery delivery, HubResponse originHub, HubResponse destinationHub, List<DeliveryRouteResponse> routes) {
        return new DeliveryDetailResponse(
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getStatus(),
                delivery.getCancellationReason(),
                delivery.getOriginHubId(),
                originHub.name(),
                delivery.getDestinationHubId(),
                destinationHub.name(),
                delivery.getDeliveryAddress(),
                delivery.getRecipientName(),
                delivery.getRecipientSlackId(),
                delivery.getCompanyDeliveryManagerId(),
                delivery.getCreatedAt(),
                delivery.getUpdatedAt(),
                routes
        );
    }
}
