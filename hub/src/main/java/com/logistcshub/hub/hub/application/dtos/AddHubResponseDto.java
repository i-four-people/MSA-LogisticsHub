package com.logistcshub.hub.hub.application.dtos;

import com.logistcshub.hub.hub.domain.mode.Hub;

public record AddHubResponseDto(
        String name,
        String address,
        double lat,
        double lng
) {

    public static AddHubResponseDto of(Hub hub) {
        return new AddHubResponseDto(
                hub.getName(),
                hub.getArea().getState().getKoreanName()
                        + " " + hub.getArea().getCity().getKoreanName()
                        + " " + hub.getAddress(),
                hub.getLat(),
                hub.getLng()
        );
    }
}
