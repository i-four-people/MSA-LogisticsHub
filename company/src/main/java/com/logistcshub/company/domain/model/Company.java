package com.logistcshub.company.domain.model;

import com.logistcshub.company.presentation.request.CompanyRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "p_companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    @Column(nullable = false)
    private UUID hubId;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;


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
        createdBy = id.toString();
        createdAt = LocalDateTime.now();
    }

    public void delete(String id) {
        deletedAt = LocalDateTime.now();
        deletedBy = id;
        isDelete = true;
    }

    public void update(String id, CompanyRequestDto companyRequestDto, Double lng, Double lat, UUID hubId) {
        updatedBy = id;
        updatedAt = LocalDateTime.now();
        this.name= companyRequestDto.name();
        this.address = companyRequestDto.address();
        this.contact = companyRequestDto.contact();
        this.companyType = companyRequestDto.companyType();
        this.lng = lng;
        this.lat = lat;
        this.hubId = hubId;
        this.isDelete = false;
    }

}
