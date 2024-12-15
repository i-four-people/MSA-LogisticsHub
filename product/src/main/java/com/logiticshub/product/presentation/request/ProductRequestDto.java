package com.logiticshub.product.presentation.request;

import com.logiticshub.product.domain.model.Product;

import java.util.UUID;

public record ProductRequestDto(
        String name,
        String description,
        Integer price,
        Integer stock,
        UUID companyId,
        UUID hubId

) {
    public Product toEntity(UUID hubId){
        return Product.builder()
                .name(this.name)
                .description(this.description)
                .price(this.price)
                .stock(this.stock)
                .companyId(this.companyId)
                .hubId(hubId)
                .build();
    }
}
