package com.logistics.delivery.application.dto.hub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HubToHubResponse {

    private HubDetail startHub;
    private HubDetail endHub;
    private List<HubDetail> stopover;
    private int totalTimeTaken;
    private int totalDistance;
    private List<HubToHubInfo> hubToHubInfoList;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HubDetail { // 허브 세부 정보

        private UUID hubId;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HubToHubInfo { // 허브 간 경로 정보

        private UUID startHubId;
        private UUID endHubId;
        private int timeTaken;
        private int distance;
    }

}
