package com.logistics.order.infrastructure.client;

import com.logistics.order.application.dto.company.CompanyResponse;
import com.logistics.order.presentation.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/api/companies/search-by-name")
    ResponseEntity<ApiResponse<List<CompanyResponse>>> findCompaniesByName(@RequestParam("name") String name);

    @GetMapping("/api/companies/{companyId}")
    ResponseEntity<ApiResponse<CompanyResponse>> findCompanyById(@PathVariable("companyId") UUID companyId);

    @PostMapping("/api/companies/batch")
    List<CompanyResponse> findCompaniesByIds(@RequestBody List<UUID> ids);

}
