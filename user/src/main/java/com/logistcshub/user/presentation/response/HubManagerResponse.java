package com.logistcshub.user.presentation.response;

import com.logistcshub.user.domain.model.HubManager;

import java.io.Serializable;
import java.util.UUID;

public record HubManagerResponse(Long userId, UUID hubId) implements Serializable {

    public static HubManagerResponse of(HubManager hubManager) {
        return new HubManagerResponse(
                hubManager.getUser().getId(),
                hubManager.getHubId()
        );
    }
}
