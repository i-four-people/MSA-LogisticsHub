package com.logistcshub.hub.area.application.dtos;

import com.logistcshub.hub.area.domain.model.Area;
import java.util.UUID;

public record AddAreaResponseDto(
        UUID id,
        String state,
        String city
) {
    public AddAreaResponseDto(Area area) {
        this(area.getId(), area.getState().getKoreanName(), area.getCity().getKoreanName());
    }
}
