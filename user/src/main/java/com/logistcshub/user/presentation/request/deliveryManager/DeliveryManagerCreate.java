package com.logistcshub.user.presentation.request.deliveryManager;

import com.logistcshub.user.domain.model.deliveryManager.DeliveryManagerType;

import java.io.Serializable;
import java.util.UUID;

public record DeliveryManagerCreate(Long userId,
                                    UUID hubId,
                                    DeliveryManagerType deliveryManagerType) implements Serializable {

}
