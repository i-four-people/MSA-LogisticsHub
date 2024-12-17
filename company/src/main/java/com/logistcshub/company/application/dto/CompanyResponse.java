package com.logistcshub.company.application.dto;

import com.logistcshub.company.domain.model.Company;

import java.util.UUID;

public record CompanyResponse(
        UUID companyId,
        String companyName
) {
    public static CompanyResponse toDto(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName()
        );
    }
}