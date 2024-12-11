package com.logistics.order.infrastructure.client;

import com.logistics.order.application.dto.CompanyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/api/companies/search-by-name")
    List<CompanyResponse> findCompaniesByName(@RequestParam("name") String name);

    @PostMapping("/companies/batch")
    List<CompanyResponse> getCompaniesByIds(@RequestBody List<UUID> ids);

}
