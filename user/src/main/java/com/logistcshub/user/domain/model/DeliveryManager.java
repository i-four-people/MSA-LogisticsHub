package com.logistcshub.user.domain.model;

import com.logistcshub.user.infrastructure.common.AuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_delivery_managers")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class DeliveryManager extends AuditEntity {

    @Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column
    private UUID hubId;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private DeliveryManagerType deliveryPersonType;

    @Column(nullable = false, unique = true)
    private Long deliverySequence;

    // 배송 담당자 등록
    public static DeliveryManager create(Long userId, UUID hubId, DeliveryManagerType deliveryPersonType, Long deliverySequence) {
        return DeliveryManager.builder()
                .id(userId)
                .userId(userId)
                .hubId(hubId)
                .deliveryPersonType(deliveryPersonType)
                .deliverySequence(userId)
                .build();
    }
}
