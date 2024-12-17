package com.logistcshub.user.presentation.request.deliveryManager;

import java.util.UUID;

public record HubManagerRequest(Long userId, UUID hubId) {
}
