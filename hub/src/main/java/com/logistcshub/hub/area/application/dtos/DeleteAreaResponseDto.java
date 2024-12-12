package com.logistcshub.hub.area.application.dtos;

import com.logistcshub.hub.area.domain.model.Area;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeleteAreaResponseDto(
        UUID id,
        String state,
        String city,
        LocalDateTime deletedAt,
        String deletedBy
) {
    public DeleteAreaResponseDto(Area area) {
        this(area.getId(), area.getState().getKoreanName(), area.getCity().getKoreanName(), area.getDeletedAt(), area.getDeletedBy());
    }
}
