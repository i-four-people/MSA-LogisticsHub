package com.logistics.delivery.presentation.auth;

public record AuthHeaderInfo(
        Long userId,
        String role
) {
}
