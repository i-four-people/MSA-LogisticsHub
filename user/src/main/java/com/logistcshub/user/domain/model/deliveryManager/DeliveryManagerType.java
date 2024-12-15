package com.logistcshub.user.domain.model.deliveryManager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryManagerType {
    HUB_PIC("HUB_PIC"),
    COMPANY_PIC("COMPANY_PIC");

    private final String type;

}
