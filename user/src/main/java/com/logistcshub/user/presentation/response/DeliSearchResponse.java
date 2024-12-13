package com.logistcshub.user.presentation.response;

import com.logistcshub.user.application.dtos.DeliveryManagerDto;

import java.io.Serializable;
import java.util.List;

public record DeliSearchResponse(List<DeliveryManagerDto> deliveryManagerDtoList) implements Serializable {
}
