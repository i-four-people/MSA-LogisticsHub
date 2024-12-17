package com.logistcshub.user.presentation.request.deliveryManager;

import com.logistcshub.user.domain.model.deliveryManager.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerUpdate(DeliveryManagerType deliveryManagerType, UUID hubId) {
}
