package com.logistcshub.user.presentation.request;

import com.logistcshub.user.domain.model.DeliveryManagerType;
import com.logistcshub.user.domain.model.DeliveryStatus;

import java.io.Serializable;
import java.util.UUID;

public record DeliSearchRequest(Long id, Long userId, UUID hubId,
                                DeliveryManagerType deliveryManagerType,
                                DeliveryStatus deliveryStatus
                                ) implements Serializable {
}
