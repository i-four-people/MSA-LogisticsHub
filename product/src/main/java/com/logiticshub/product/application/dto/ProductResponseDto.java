package com.logiticshub.product.application.dto;

import com.logiticshub.product.domain.model.Product;

import java.util.UUID;

public record ProductResponseDto(
        UUID id,
        String name,
        String description,
        Integer stock,
        UUID companyId,
        UUID hubId,
        boolean isDelete
) {
    public static ProductResponseDto toDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getStock(),
                product.getCompanyId(),
                product.getHubId(),
                product.isDelete()
        );
    }
}
