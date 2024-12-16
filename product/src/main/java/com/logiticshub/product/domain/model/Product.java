package com.logiticshub.product.domain.model;

import com.logiticshub.product.presentation.request.ProductRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "p_products")
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private UUID hubId;

    @Builder.Default
    private boolean isDelete = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @LastModifiedBy
    private String updatedBy;

    private LocalDateTime deletedAt;

    private String deletedBy;


    public void create(Long id) {
        createdAt = LocalDateTime.now();
        createdBy = id.toString();
    }

    public void update(Long id, ProductRequestDto productRequestDto, UUID hubId) {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = id.toString();
        this.name= productRequestDto.name();
        this.description = productRequestDto.description();
        this.price = productRequestDto.price();
        this.stock = productRequestDto.stock();
        this.companyId = productRequestDto.companyId();
        this.hubId = hubId;
        this.isDelete = false;
    }

    public void delete(Long userId) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = userId.toString();
        this.isDelete = true;
    }
}
