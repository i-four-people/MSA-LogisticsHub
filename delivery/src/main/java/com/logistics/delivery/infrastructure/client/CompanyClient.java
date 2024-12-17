package com.logistics.delivery.infrastructure.client;

import com.logistics.delivery.application.dto.company.CompanyResponse;
import com.logistics.delivery.presentation.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/api/companies/{companyId}")
    ResponseEntity<ApiResponse<CompanyResponse>> findCompanyById(@PathVariable("companyId") UUID companyId);
}
