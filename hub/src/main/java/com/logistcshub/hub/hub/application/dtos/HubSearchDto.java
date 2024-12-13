package com.logistcshub.hub.hub.application.dtos;

import com.logistcshub.hub.area.domain.model.type.City;
import com.logistcshub.hub.area.domain.model.type.State;
import java.util.UUID;

public record HubSearchDto(
        UUID id,
        String name,
        State state,
        City city,
        String address,
        double lat,
        double lng
) {

}
