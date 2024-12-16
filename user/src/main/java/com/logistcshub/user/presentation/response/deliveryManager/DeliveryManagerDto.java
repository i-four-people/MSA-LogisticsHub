package com.logistcshub.user.presentation.response.deliveryManager;

import com.logistcshub.user.domain.model.deliveryManager.DeliveryManager;
import com.logistcshub.user.domain.model.deliveryManager.DeliveryManagerType;
import com.logistcshub.user.domain.model.deliveryManager.DeliveryStatus;
import com.querydsl.core.annotations.QueryProjection;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryManagerDto(Long id,
                                 String ksuid,
                                 Long userId,
                                 UUID hubId,
                                 DeliveryManagerType deliveryManagerType,
                                 DeliveryStatus deliveryStatus,
                                 LocalDateTime createdAt,
                                 String createBy,
                                 LocalDateTime updatedAt,
                                 String updatedBy) implements Serializable {


    public static DeliveryManagerDto from(DeliveryManager deliveryManager) {
        return new DeliveryManagerDto(
                deliveryManager.getId(),
                deliveryManager.getKsuid(),
                deliveryManager.getUserId(),
                deliveryManager.getHubId(),
                deliveryManager.getDeliveryManagerType(),
                deliveryManager.getStatus(),
                deliveryManager.getCreatedAt(),
                deliveryManager.getCreatedBy(),
                deliveryManager.getUpdatedAt(),
                deliveryManager.getUpdatedBy()
        );
    }

    @QueryProjection
    public DeliveryManagerDto(Long id,
                              String ksuid,
                              Long userId,
                              UUID hubId,
                              DeliveryManagerType deliveryManagerType,
                              DeliveryStatus deliveryStatus,
                              LocalDateTime createdAt,
                              String createBy,
                              LocalDateTime updatedAt,
                              String updatedBy){
        this.id = id;
        this.ksuid = ksuid;
        this.userId = userId;
        this.hubId = hubId;
        this.deliveryManagerType = deliveryManagerType;
        this.deliveryStatus = deliveryStatus;
        this.createdAt = createdAt;
        this.createBy = createBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }
}
