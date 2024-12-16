package com.logiticshub.product.application.dto;

import com.logiticshub.product.domain.model.Product;

import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String productName,
        int stock,
        UUID companyId
) {
    public static ProductResponse toDto(Product product){
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getStock(),
                product.getCompanyId()
        );
    }
}