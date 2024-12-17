package com.logistics.delivery.domain.model;

import com.logistics.delivery.application.dto.company.CompanyResponse;
import com.logistics.delivery.application.dto.event.consume.OrderCreateConsume;
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

    @Comment("업체 배송 담당자 ID")
    private Long companyDeliveryManagerId;

    @Comment("삭제 여부")
    private boolean isDelete;

    @Builder
    private Delivery(UUID orderId, DeliveryStatus status, String cancellationReason, UUID originHubId, UUID destinationHubId, String deliveryAddress, String recipientName, String recipientSlackId) {
        this.orderId = orderId;
        this.status = status;
        this.cancellationReason = cancellationReason;
        this.originHubId = originHubId;
        this.destinationHubId = destinationHubId;
        this.deliveryAddress = deliveryAddress;
        this.recipientName = recipientName;
        this.recipientSlackId = recipientSlackId;
    }

    /**
     * 배송 생성하는 정적 팩토리 메서드
     *
     * @param event            주문 생성 event
     * @param recipientCompany 수령 업체 정보
     * @param supplyCompany    공급 업체 정보
     * @return Delivery
     */
    public static Delivery create(OrderCreateConsume event, CompanyResponse recipientCompany, CompanyResponse supplyCompany) {
        return Delivery.builder()
                .orderId(event.orderId())
                .status(DeliveryStatus.PENDING)
                .originHubId(supplyCompany.hubId())
                .destinationHubId(recipientCompany.hubId())
                .recipientName(event.recipientName())
                .recipientSlackId(event.recipientSlackId())
                .build();
    }

    /**
     * 업체 배송 담당자를 배정하는 메서드
     *
     * @param assignedManagerId 업체 배송 담당자 ID
     */
    public void assignDeliveryCompanyManager(Long assignedManagerId) {
        this.companyDeliveryManagerId = assignedManagerId;
    }

}
