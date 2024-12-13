package com.logistcshub.user.presentation.request;

import com.logistcshub.user.domain.model.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerUpdate(DeliveryManagerType deliveryManagerType, UUID hubId) {
}
