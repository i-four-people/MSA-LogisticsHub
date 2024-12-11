package com.logistcshub.company.presentation.request;

import com.logistcshub.company.domain.model.CompanyType;

import java.util.UUID;

public record CompanyRequestDto (

        String name,
        String address,
        String contact,
        CompanyType companyType,
        UUID hubId
){


}
