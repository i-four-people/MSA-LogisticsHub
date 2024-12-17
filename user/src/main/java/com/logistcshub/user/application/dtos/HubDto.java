package com.logistcshub.user.application.dtos;

import java.io.Serializable;
import java.util.UUID;

public record HubDto(UUID id) implements Serializable {

    public UUID getId() {
        return id;
    }
}
