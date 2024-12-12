package com.logistcshub.user.application.client;

import com.logistcshub.user.infrastructure.common.ApiResponse;
import com.logistcshub.user.presentation.request.HubRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/hubs/{id}")
    ApiResponse<HubRequest> getHub(@PathVariable("id") UUID id);
}
