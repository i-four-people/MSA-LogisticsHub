package com.logistcshub.company.presentation.response;

import com.logistcshub.company.domain.model.Company;
import com.logistcshub.company.domain.model.CompanyType;

import java.util.UUID;

public record CompanyResponseDto (
        UUID id,
        String name,
        String address,
        String contact,
        CompanyType companyType,
        UUID hubId,
        boolean isDelete

) {
    public static CompanyResponseDto toDto(Company company) {
        return new CompanyResponseDto(
                company.getId(),
                company.getName(),
                company.getAddress(),
                company.getContact(),
                company.getCompanyType(),
                company.getHubId(),
                company.getIsDelete()
        );
    }
}
