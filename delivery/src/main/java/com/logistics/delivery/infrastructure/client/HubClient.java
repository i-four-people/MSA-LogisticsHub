package com.logistics.delivery.infrastructure.client;

import com.logistics.delivery.application.dto.hub.HubResponse;
import com.logistics.delivery.application.dto.hub.HubToHubResponse;
import com.logistics.delivery.presentation.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/hub-transfers/hub-to-hub")
    ResponseEntity<ApiResponse<HubToHubResponse>> getHubToHubRoutes(@RequestParam("startHubId") UUID startHubId,
                                                                    @RequestParam("endHubId") UUID endHubId);

    @GetMapping("/api/hubs/{hubId}")
    ResponseEntity<ApiResponse<HubResponse>> getHub(@PathVariable("hubId") UUID hubId);

    @GetMapping("/api/hubs/list")
    ResponseEntity<ApiResponse<List<HubResponse>>> getHubsToHubIds(@RequestBody List<UUID> hubIds);
}
