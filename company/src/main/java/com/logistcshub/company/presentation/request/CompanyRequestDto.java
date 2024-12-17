package com.logistcshub.company.presentation.request;

import com.logistcshub.company.domain.model.Company;
import com.logistcshub.company.domain.model.CompanyType;

import java.util.UUID;

public record CompanyRequestDto(
        String name,
        String address,
        String contact,
        CompanyType companyType
) {
    public Company toEntity(double lng, double lat, UUID hubId) {
        return Company.builder()
                .name(this.name)
                .address(this.address)
                .lng(lng)
                .lat(lat)
                .contact(this.contact)
                .companyType(this.companyType)
                .hubId(hubId)
                .build();
    }
}
