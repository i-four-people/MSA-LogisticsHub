package com.logistcshub.hub.hub_transfer.application.dtos;

import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;

import java.util.UUID;


public record UpdateHubTransferResponseDto (
        UUID id,
        UUID startHubId,
        String startHubName,
        UUID endHubId,
        String endHubName,
        Integer time_taken,
        Integer distance
) {
    public static UpdateHubTransferResponseDto of(HubTransfer transfer, Hub startHub, Hub endHub){
        return new UpdateHubTransferResponseDto(
                transfer.getId(),
                startHub.getId(),
                startHub.getName(),
                endHub.getId(),
                endHub.getName(),
                transfer.getTimeTaken(),
                transfer.getDistance()
        );
    }
}