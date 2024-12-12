package com.logistics.order.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClient {

    @PatchMapping("/products/{productId}/stock")
    void decreaseStock(@PathVariable("productId") UUID productId, @RequestParam int quantity);

}
