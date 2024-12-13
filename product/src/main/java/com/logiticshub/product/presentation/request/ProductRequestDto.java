package com.logiticshub.product.presentation.request;

import com.logiticshub.product.domain.model.Product;

import java.util.UUID;

public record ProductRequestDto(
        String name,
        String description,
        Integer stock,
        UUID companyId,
        UUID hubId

) {
    public Product toEntity(){
        return Product.builder()
                .name(this.name)
                .description(this.description)
                .stock(this.stock)
                .companyId(this.companyId)
                .hubId(this.hubId)
                .build();
    }
}