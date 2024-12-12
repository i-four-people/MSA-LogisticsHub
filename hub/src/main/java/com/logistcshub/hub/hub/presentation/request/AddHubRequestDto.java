package com.logistcshub.hub.hub.presentation.request;

import com.logistcshub.hub.hub.domain.mode.Hub;

public record AddHubRequestDto(
        String name,
        String address
) {

    public Hub from() {
        return Hub.builder()
                .name(name)
                .address(address)
                .build();
    }
}
