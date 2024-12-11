package com.logistcshub.hub.hub.application.dtos;

import com.logistcshub.hub.hub.domain.mode.Hub;

public record AddHubResponseDto(
        String name,
        double lat,
        double lng,
        String address
) {

    public static AddHubResponseDto of(Hub hub) {
        return new AddHubResponseDto(
                hub.getName(),
                hub.getLat(),
                hub.getLng(),
                hub.getArea().getState().getKoreanName()
                        + " " + hub.getArea().getCity().getKoreanName()
                        + " " + hub.getAddress()
        );
    }
}
