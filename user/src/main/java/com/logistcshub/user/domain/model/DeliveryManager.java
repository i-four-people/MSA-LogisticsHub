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
    private Long id; // 사용자 관리 엔티티의 사용자 Id

    @Column(nullable = false, unique = true)
    private String ksuid; // Ksuid를 별도로 저장

    @Column(nullable = false)
    private Long userId;

    @Column
    private UUID hubId;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private DeliveryManagerType deliveryPersonType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    // 배송 담당자 등록
    public static DeliveryManager from(String ksuid, Long userId, UUID hubId, DeliveryManagerType deliveryPersonType) {
        return DeliveryManager.builder()
                .id(userId)
                .ksuid(ksuid)
                .userId(userId)
                .hubId(hubId)
                .deliveryPersonType(deliveryPersonType)
                .status(DeliveryStatus.COMPLETED)
                .build();
    }
}
