package com.logistcshub.user.presentation.response.deliveryManager;

import com.logistcshub.user.domain.model.deliveryManager.DeliveryManager;
import com.logistcshub.user.domain.model.user.User;

import java.util.UUID;

public record DeliveryManagerResponse(Long id,  // 배송 담당자 ID
                                      String name,  // 배송 담당자 이름
                                      UUID hubId,
                                      String slackId,  // 배송 담당자 Slack ID
                                      String sequence,   // 순번
                                      String type,  // 배송 담당자 타입
                                      String status  // 배송 담당자 상태
) {

    public static DeliveryManagerResponse from(DeliveryManager deliveryManager, User user) {
        return new DeliveryManagerResponse(
                deliveryManager.getId(),
                user.getUsername(),
                deliveryManager.getHubId(),
                user.getSlackId(),
                deliveryManager.getKsuid(),
                deliveryManager.getDeliveryManagerType().name(),
                deliveryManager.getStatus().name()
        );
    }
}
