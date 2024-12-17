package com.logistics.delivery.infrastructure.client;

import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerResponse;
import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerType;
import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service")
public interface DeliveryManagerClient {

    @GetMapping("/api/delivery-managers/available-manager/list")
    List<DeliveryManagerResponse> findAvailableManagers(@RequestParam(defaultValue = "HUB_PIC") DeliveryManagerType type);

    @PutMapping("/api/delivery-managers/{deliveryManagerId}/hub")
    void updateHubForManager(@PathVariable("deliveryManagerId") Long deliveryManagerId,
                             @RequestBody DeliveryManagerUpdateRequest request);
}
