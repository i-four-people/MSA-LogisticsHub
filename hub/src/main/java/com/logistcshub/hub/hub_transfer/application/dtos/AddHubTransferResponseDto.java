package com.logistcshub.hub.hub_transfer.application.dtos;

import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;
import java.util.UUID;

public record AddHubTransferResponseDto(
        UUID id,
        UUID startHubId,
        String startHubName,
        UUID endHubId,
        String endHubName,
        Integer time_taken,
        Integer distance
) {

    public static AddHubTransferResponseDto of(HubTransfer transfer){
        return new AddHubTransferResponseDto(
                transfer.getId(),
                transfer.getStartHub().getId(),
                transfer.getStartHub().getName(),
                transfer.getEndHub().getId(),
                transfer.getEndHub().getName(),
                transfer.getTimeTaken(),
                transfer.getDistance()
        );
    }

    public static AddHubTransferResponseDto of(HubTransfer transfer, Hub startHub, Hub endHub){
        return new AddHubTransferResponseDto(
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
