package com.logistics.delivery.infrastructure.client;

import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerResponse;
import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface DeliveryManagerClient {

    @GetMapping("/api/delivery-managers/available-manager")
    List<DeliveryManagerResponse> findAvailableManagers(@RequestParam(defaultValue = "HUB_PIC") DeliveryManagerType type);
}
