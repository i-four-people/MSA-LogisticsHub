package com.logistcshub.user.presentation.request.deliveryManager;

import com.logistcshub.user.domain.model.deliveryManager.DeliveryManagerType;
import com.logistcshub.user.domain.model.deliveryManager.DeliveryStatus;

import java.io.Serializable;
import java.util.UUID;

public record DeliSearchRequest(Long id, Long userId, UUID hubId,
                                DeliveryManagerType deliveryManagerType,
                                DeliveryStatus deliveryStatus
                                ) implements Serializable {
}
