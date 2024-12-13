package com.logistcshub.hub.common.domain.model.dtos;

import java.util.List;
import lombok.AllArgsConstructor;

public record KakaoRoadRequestDto (
        Coordinate origin,
        Coordinate destination,
        boolean summary
) {

    @AllArgsConstructor
    public static class Coordinate {
        double x;
        double y;
    }

    public KakaoRoadRequestDto(Coordinate origin, Coordinate destination) {
        this(origin, destination, false);
    }
}
