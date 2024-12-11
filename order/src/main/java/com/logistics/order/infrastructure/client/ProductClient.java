package com.logistics.order.infrastructure.client;

import com.logistics.order.application.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClient {

    @PatchMapping("/api/products/{productId}/stock")
    void decreaseStock(@PathVariable("productId") UUID productId, @RequestParam int quantity);

    @PostMapping("/products/batch")
    List<ProductResponse> getProductsByIds(@RequestBody List<UUID> ids);

}
