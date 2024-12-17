package com.logistics.order.presentation.auth;

public record AuthHeaderInfo(
        Long userId,
        String role
) {
}
