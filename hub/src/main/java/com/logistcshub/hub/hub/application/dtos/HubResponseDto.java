package com.logistcshub.hub.hub.application.dtos;

import com.logistcshub.hub.area.domain.model.type.State;
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
        String fullAddress = State.getKoreanName(hub.getArea().getState())
                + " " + hub.getArea().getCity().getKoreanName()
                + " " + hub.getAddress();
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

    public static HubResponseDto of(HubSearchDto hub) {
        String fullAddress = State.getKoreanName(hub.state())
                + " " + hub.city().getKoreanName()
                + " " + hub.address();
        return new HubResponseDto(
                hub.id(),
                hub.name(),
                fullAddress,
                hub.lat(),
                hub.lng()
        );
    }

}
