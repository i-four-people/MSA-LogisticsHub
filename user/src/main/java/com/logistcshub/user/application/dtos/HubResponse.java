package com.logistcshub.user.application.dtos;

import java.util.UUID;

public record HubResponse(UUID id, String name, String address) {
}
