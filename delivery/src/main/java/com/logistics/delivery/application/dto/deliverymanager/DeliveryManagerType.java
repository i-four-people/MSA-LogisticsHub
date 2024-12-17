package com.logistics.delivery.application.dto.deliverymanager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryManagerType {
    HUB_PIC("HUB_PIC"),
    COMPANY_PIC("COMPANY_PIC");

    private final String type;

}
