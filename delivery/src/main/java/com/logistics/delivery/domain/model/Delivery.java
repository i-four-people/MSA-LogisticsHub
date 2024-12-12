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
@Table(name = "p_deliveries")
@Entity
public class Delivery extends AuditingFields {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Comment("배송 고유 ID")
    private UUID id;

    @Comment("주문 ID")
    private UUID orderId;

    @Comment("배송 상태")
    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'PENDING'")
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Comment("취소 사유")
    private String cancellationReason;

    @Comment("출발 허브 ID")
    private UUID originHubId;

    @Comment("도착 허브 ID")
    private UUID destinationHubId;

    @Comment("배송지 주소")
    private String deliveryAddress;

    @Comment("수령자명")
    private String recipientName;

    @Comment("수령자 Slack Id")
    private String recipientSlackId;

    @Comment("삭제 여부")
    private boolean isDelete;

}
