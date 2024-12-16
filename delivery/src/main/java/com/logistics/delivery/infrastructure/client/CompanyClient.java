package com.logistics.delivery.infrastructure.client;

import com.logistics.delivery.application.dto.company.CompanyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/api/companies/{companyId}")
    CompanyResponse findCompanyById(@PathVariable("companyId") UUID companyId);
}
