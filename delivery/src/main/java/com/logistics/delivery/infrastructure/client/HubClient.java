package com.logistics.delivery.infrastructure.client;

import com.logistics.delivery.application.dto.hub.HubToHubResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/hub-tranfers/routes")
    HubToHubResponse getHubToHubRoutes(UUID startHubId, UUID endHubId);
}
