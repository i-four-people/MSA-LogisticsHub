package com.logistcshub.user.presentation.request;

import com.logistcshub.user.domain.model.DeliveryManagerType;
import com.logistcshub.user.domain.model.DeliveryStatus;

import java.io.Serializable;
import java.util.UUID;

public record DeliveryManagerCreate(Long userId,
                                    UUID hubId,
                                    DeliveryManagerType deliveryManagerType) implements Serializable {
}
