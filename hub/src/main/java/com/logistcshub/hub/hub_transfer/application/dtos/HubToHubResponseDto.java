package com.logistcshub.hub.hub_transfer.application.dtos;

import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record HubToHubResponseDto(
        HubToHub startHub,
        HubToHub endHub,
        List<HubToHub> stopover,
        Integer totalTimeTaken,
        Integer totalDistance,
        List<HubToHubInfo> hubToHubInfoList
) implements Serializable {

    public static HubToHubResponseDto of(HubTransfer startToEnd, Hub startHub, Hub endHub) {
        List<HubToHubInfo> hubToHubInfoList = new ArrayList<>();
        hubToHubInfoList.add(new HubToHubInfo(
                startHub.getId(),
                endHub.getId(),
                startToEnd.getTimeTaken(),
                startToEnd.getDistance()
        ));

        return new HubToHubResponseDto(
                HubToHub.of(startHub),
                HubToHub.of(endHub),
                null,
                startToEnd.getTimeTaken(),
                startToEnd.getDistance(),
                hubToHubInfoList
        );
    }

    public static record HubToHub(
            UUID hubId,
            String name
    ) implements Serializable{
        public static HubToHub of(Hub hub) {
            return new HubToHub(
                    hub.getId(),
                    hub.getName()
            );
        }
    }

    public static record HubToHubInfo(
            UUID startHubId,
            UUID endHubId,
            Integer timeTaken,
            Integer distance
    ) implements Serializable {
        public static HubToHubInfo of(HubTransfer hubTransfer) {
            return new HubToHubInfo(
                    hubTransfer.getStartHub().getId(),
                    hubTransfer.getEndHub().getId(),
                    hubTransfer.getTimeTaken(),
                    hubTransfer.getDistance()
            );
        }
    }
}
