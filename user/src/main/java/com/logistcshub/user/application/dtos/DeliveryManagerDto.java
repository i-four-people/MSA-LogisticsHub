package com.logistcshub.user.application.dtos;

import com.logistcshub.user.domain.model.DeliveryManager;
import com.logistcshub.user.domain.model.DeliveryManagerType;
import com.querydsl.core.annotations.QueryProjection;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryManagerDto(Long id,
                                 Long userId,
                                 UUID hubId,
                                 DeliveryManagerType deliveryManagerType,
                                 LocalDateTime createdAt,
                                 String createBy,
                                 LocalDateTime updatedAt,
                                 String updatedBy) implements Serializable {


    public static DeliveryManagerDto from(DeliveryManager deliveryManager) {
        return new DeliveryManagerDto(
                deliveryManager.getId(),
                deliveryManager.getUserId(),
                deliveryManager.getHubId(),
                deliveryManager.getDeliveryPersonType(),
                deliveryManager.getCreatedAt(),
                deliveryManager.getCreatedBy(),
                deliveryManager.getUpdatedAt(),
                deliveryManager.getUpdatedBy()
        );
    }

    @QueryProjection
    public DeliveryManagerDto(Long id,
                              Long userId,
                              UUID hubId,
                              DeliveryManagerType deliveryManagerType,
                              LocalDateTime createdAt,
                              String createBy,
                              LocalDateTime updatedAt,
                              String updatedBy){
        this.id = id;
        this.userId = userId;
        this.hubId = hubId;
        this.deliveryManagerType = deliveryManagerType;
        this.createdAt = createdAt;
        this.createBy = createBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }
}
