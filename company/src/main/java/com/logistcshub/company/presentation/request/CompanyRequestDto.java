package com.logistcshub.company.presentation.request;

import com.logistcshub.company.domain.model.Company;
import com.logistcshub.company.domain.model.CompanyType;

import java.util.UUID;

public record CompanyRequestDto(
        String name,
        String address,
        String contact,
        CompanyType companyType,
        UUID hubId
) {
    public Company toEntity() {
        return Company.builder()
                .name(this.name)
                .address(this.address)
                .contact(this.contact)
                .companyType(this.companyType)
                .hubId(this.hubId)
                .build();
    }
}
