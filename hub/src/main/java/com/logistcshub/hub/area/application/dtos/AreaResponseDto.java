package com.logistcshub.hub.area.application.dtos;

import com.logistcshub.hub.area.domain.model.Area;
import java.time.LocalDateTime;
import java.util.UUID;


public record AreaResponseDto(
        UUID id,
        String state,
        String city,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy
) {
    public static AreaResponseDto of(Area area) {
        return new AreaResponseDto(
                area.getId(),
                area.getState().getKoreanName(),
                area.getCity().getKoreanName(),
                area.getCreatedAt(),
                area.getCreatedBy(),
                area.getUpdatedAt(),
                area.getUpdatedBy()
        );
    }
}
