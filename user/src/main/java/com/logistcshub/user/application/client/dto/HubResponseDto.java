package com.logistcshub.user.application.client.dto;

import java.util.UUID;

public record HubResponseDto(
        UUID id,
        String name,
        String address,
        double lat,
        double lng
){}
