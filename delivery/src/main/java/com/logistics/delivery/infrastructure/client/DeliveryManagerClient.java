package com.logistics.delivery.infrastructure.client;

import com.logistics.delivery.application.dto.user.DeliveryManagerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user-service")
public interface DeliveryManagerClient {

    @GetMapping("/api/delivery-managers/available-manager")
    List<DeliveryManagerResponse> findAvailableManagers();
}
