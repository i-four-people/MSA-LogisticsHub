package com.logistcshub.user.application.dtos;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class TokenDto {
    private String accessToken;

    public static TokenDto of(String accessToken) {
        return TokenDto.builder().accessToken(accessToken).build();
    }

    @Override
    public String toString() {
        return accessToken;
    }
}
