package com.logistcshub.company.application.client;

import com.logistcshub.company.application.dto.HubResponseDto;
import com.logistcshub.company.application.dto.SuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/hubs/company-address")
    ResponseEntity<SuccessResponse<HubResponseDto>> getHubFromCompanyAddress(
            @RequestHeader(value = "X-USER-ID") Long userId,
            @RequestHeader(value = "X-USER-ROLE") String role,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "lat") double lat,
            @RequestParam(name = "lng") double lng
    );
}
