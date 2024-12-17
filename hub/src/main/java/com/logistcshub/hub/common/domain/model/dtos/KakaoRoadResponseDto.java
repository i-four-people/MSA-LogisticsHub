package com.logistcshub.hub.common.domain.model.dtos;

import com.logistcshub.hub.common.domain.model.dtos.KakaoResponse.Route.Summary;

public record KakaoRoadResponseDto (
        Integer distance,
        Integer duration
) {
    public static KakaoRoadResponseDto of(Summary summary) {
        return new KakaoRoadResponseDto(
                summary.distance(),
                summary.duration()
        );
    }
}
