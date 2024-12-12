package com.logistcshub.hub.hub.application.dtos;

import com.logistcshub.hub.hub.domain.mode.Hub;
import java.util.UUID;

public record HubResponseDto(
        UUID id,
        String name,
        String address,
        double lat,
        double lng
) {

    public static HubResponseDto of(Hub hub) {
        return new HubResponseDto(
                hub.getId(),
                hub.getName(),
                hub.getArea().getState().getKoreanName()
                        + " " + hub.getArea().getCity().getKoreanName()
                        + " " + hub.getAddress(),
                hub.getLat(),
                hub.getLng()
        );
    }

}
