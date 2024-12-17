package com.logistcshub.user.presentation.response.deliveryManager;

import java.io.Serializable;
import java.util.List;

public record DeliSearchResponse(List<DeliveryManagerDto> deliveryManagerDtoList) implements Serializable {
}
