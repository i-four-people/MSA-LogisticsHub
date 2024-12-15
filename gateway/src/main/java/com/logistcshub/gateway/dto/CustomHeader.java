package com.logistcshub.gateway.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomHeader {
    private String token;
    private String userId;
    private String role;
}