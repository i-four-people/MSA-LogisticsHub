package com.logistcshub.user.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {
    MASTER(Authority.MASTER),
    COMPANY_MANAGER(Authority.COMPANY_MANAGER),
    DELIVERY_MANAGER(Authority.DELIVERY_MANAGER),
    HUB_MANAGER(Authority.HUB_MANAGER);

    private final String authority;

    public static class Authority {
        public static final String MASTER = "MASTER";
        public static final String COMPANY_MANAGER = "COMPANY_MANAGER";
        public static final String DELIVERY_MANAGER = "DELIVERY_MANAGER";
        public static final String HUB_MANAGER = "HUB_MANAGER";
    }
}
