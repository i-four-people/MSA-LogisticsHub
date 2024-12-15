package com.logistcshub.hub.hub_transfer.presentation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record AddHubTransferRequestDto(
        @JsonProperty("start-hub-id") UUID startHubId,
        @JsonProperty("end-hub-id") UUID endHubId
) {
}
