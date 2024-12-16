package com.logistics.delivery.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_delivery_routes")
@Entity
public class DeliveryRoute extends AuditingFields {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Comment("배송 ID")
    private UUID deliveryId;

    @Comment("배송 담당자 ID")
    private UUID deliveryManagerId;

    @Comment("배송 경로 상 순번")
    private int sequence;

    @Comment("출발 허브 ID")
    private UUID startHubId;

    @Comment("도착 허브 ID")
    private UUID endHubId;

    @Comment("경로 상 예상 거리 (km)")
    private float estimatedDistance;

    @Comment("경로 상 예상 소요 시간 (분)")
    private float estimatedDuration;

    @Comment("실제 거리")
    private float actualDistance;

    @Comment("실제 소요 시간")
    private float actualDuration;

    @Comment("경로 상태")
    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'PENDING'")
    @Enumerated(EnumType.STRING)
    private RouteStatus status;

    @Comment("삭제 여부")
    private boolean isDelete;

}
