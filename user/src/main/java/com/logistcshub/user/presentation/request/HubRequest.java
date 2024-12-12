package com.logistcshub.user.presentation.request;

import com.logistcshub.user.application.dtos.HubDto;

import java.io.Serializable;

public record HubRequest(int code, String message, com.logistcshub.user.application.dtos.HubDto HubDto) implements Serializable {
}
