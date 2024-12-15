package com.logiticshub.product.application.dto;

import java.util.UUID;

public record CompanyResponseDto(
        UUID id,
        String name,
        String address,
        String contact,
        CompanyType companyType,
        UUID hubId,
        boolean isDelete

) {
    public enum CompanyType {
        PRODUCTION_COMPANY, RECEIVING_COMPANY;
    }
}
