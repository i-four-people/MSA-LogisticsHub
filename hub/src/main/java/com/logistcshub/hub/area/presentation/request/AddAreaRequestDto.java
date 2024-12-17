package com.logistcshub.hub.area.presentation.request;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.area.domain.model.type.City;
import com.logistcshub.hub.area.domain.model.type.State;

public record AddAreaRequestDto(
        String state,
        String city
) {
    public Area toEntity() {
        State findState = State.findState(state);
        City findCity = City.findCity(city, findState);

        return Area.builder()
                .state(findState)
                .city(findCity)
                .build();
    }
}
