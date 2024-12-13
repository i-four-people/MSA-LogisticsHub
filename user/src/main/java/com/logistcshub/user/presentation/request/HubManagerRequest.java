package com.logistcshub.user.presentation.request;

import java.util.UUID;

public record HubManagerRequest(Long userId, UUID hubId) {
}
