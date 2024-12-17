package com.logistcshub.hub.hub_transfer.application.dtos;

import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;

import java.util.Optional;
import java.util.UUID;

public record HubTransferResponseDto(
        UUID startHubId,
        String startHubName,
        UUID endHubId,
        String endHubName,
        Integer time_taken,
        Integer distance
) {
    public static HubTransferResponseDto of(HubTransfer hubTransfer) {
        return new HubTransferResponseDto(
                hubTransfer.getStartHub().getId(),
                hubTransfer.getStartHub().getName(),
                hubTransfer.getEndHub().getId(),
                hubTransfer.getEndHub().getName(),
                hubTransfer.getTimeTaken(),
                hubTransfer.getDistance()
        );
    }
}
