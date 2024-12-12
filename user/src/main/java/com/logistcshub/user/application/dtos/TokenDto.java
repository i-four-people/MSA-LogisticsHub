package com.logistcshub.user.application.dtos;

import lombok.*;

public record TokenDto(String accessToken) {
    public static TokenDto of(String accessToken) {
        return new TokenDto(accessToken);
    }

    @Override
    public String toString() {
        return accessToken;
    }
}
