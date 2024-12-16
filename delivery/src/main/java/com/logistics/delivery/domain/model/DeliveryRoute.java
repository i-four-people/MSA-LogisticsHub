package com.logistics.delivery.domain.model;

import com.logistics.delivery.application.dto.hub.HubToHubResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    private Long deliveryManagerId;

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

    @Builder
    private DeliveryRoute(UUID deliveryId, int sequence, UUID startHubId, UUID endHubId, float estimatedDistance, float estimatedDuration, RouteStatus status) {
        this.deliveryId = deliveryId;
        this.sequence = sequence;
        this.startHubId = startHubId;
        this.endHubId = endHubId;
        this.estimatedDistance = estimatedDistance;
        this.estimatedDuration = estimatedDuration;
        this.status = status;
    }

    public static DeliveryRoute create(Delivery delivery, int sequence, HubToHubResponse.HubDetail startHub,
                              HubToHubResponse.HubDetail endHub, HubToHubResponse.HubToHubInfo hubToHubInfo) {
        return DeliveryRoute.builder()
                .deliveryId(delivery.getId())
                .sequence(sequence)
                .startHubId(startHub.getHubId())
                .endHubId(endHub.getHubId())
                .estimatedDistance(hubToHubInfo.getDistance())
                .estimatedDuration(hubToHubInfo.getTimeTaken())
                .status(RouteStatus.PENDING)
                .build();
    }

    /**
     * 배송 경로에 배송 담당 매니저를 배정하는 메서드
     *
     * @param assignedManagerId 배송 담당자 ID
     */
    public void assignManager(Long assignedManagerId) {
        this.deliveryManagerId = assignedManagerId;
        this.status = RouteStatus.ASSIGNED;
    }
}
