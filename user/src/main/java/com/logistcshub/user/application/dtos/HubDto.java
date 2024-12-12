package com.logistcshub.user.application.dtos;

import java.io.Serializable;
import java.util.UUID;

public record HubDto(UUID id, String name, String address, double let, double lng) implements Serializable {
}
