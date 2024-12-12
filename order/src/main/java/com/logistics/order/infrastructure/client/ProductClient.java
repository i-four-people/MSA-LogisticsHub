package com.logistics.order.infrastructure.client;

import com.logistics.order.application.dto.product.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClient {

    @PatchMapping("/api/products/{productId}/stock")
    void decreaseStock(@PathVariable("productId") UUID productId, int quantity);

    @PostMapping("/api/products/batch")
    List<ProductResponse> findProductsByIds(@RequestBody List<UUID> ids);

    @GetMapping("/api/products/{productId}")
    ProductResponse findProductById(@PathVariable("productId") UUID productId);
}
