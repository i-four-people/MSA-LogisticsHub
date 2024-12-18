package com.logistics.order.infrastructure.client;

import com.logistics.order.application.dto.product.ProductIdsResponse;
import com.logistics.order.application.dto.product.ProductResponse;
import com.logistics.order.presentation.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClient {

    @PostMapping("/api/products/batch")
    List<ProductIdsResponse> findProductsByIds(@RequestBody List<UUID> ids);

    @GetMapping("/api/products/{productId}")
    ResponseEntity<ApiResponse<ProductResponse>> findProductById(@PathVariable("productId") UUID productId);

}
