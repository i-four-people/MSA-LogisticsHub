package com.logistics.order.infrastructure.client;

import com.logistics.order.application.dto.company.CompanyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/api/companies/search-by-name")
    List<CompanyResponse> findCompaniesByName(@RequestParam("name") String name);

    @GetMapping("/api/companies/{companyId}")
    CompanyResponse findCompanyById(@PathVariable("companyId") UUID companyId);

    @PostMapping("/api/companies/batch")
    List<CompanyResponse> findCompaniesByIds(@RequestBody List<UUID> ids);

}
