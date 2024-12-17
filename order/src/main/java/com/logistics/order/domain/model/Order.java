package com.logistics.order.domain.model;

import com.logistics.order.application.dto.order.OrderCreateRequest;
import com.logistics.order.application.dto.order.OrderUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_orders")
@Entity
public class Order extends AuditingFields {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Comment("주문 고유 ID")
    private UUID id;

    @Comment("수령 업체 ID")
    private UUID recipientCompanyId;

    @Comment("공급 업체 ID")
    private UUID supplyCompanyId;

    @Comment("배송 ID")
    private UUID deliveryId;

    @Comment("상품 ID")
    private UUID productId;

    @Comment("상품 가격")
    private BigDecimal price;

    @Comment("주문 수량")
    private int quantity;

    @Comment("요청 사항")
    private String requestNotes;

    @Comment("주문 상태")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Comment("삭제 여부")
    private boolean isDelete;

    @Builder
    private Order(UUID supplyCompanyId, UUID recipientCompanyId, UUID deliveryId, UUID productId, BigDecimal price, int quantity, String requestNotes) {
        this.supplyCompanyId = supplyCompanyId;
        this.recipientCompanyId = recipientCompanyId;
        this.deliveryId = deliveryId;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.requestNotes = requestNotes;
        this.status = OrderStatus.PENDING;
    }

    /**
     * 주문 생성하는 정적 팩토리 메서드
     *
     * @param request 주문 생성 request
     * @return Order
     */
    public static Order create(OrderCreateRequest request, UUID supplyCompanyId) {
        return Order.builder()
                .recipientCompanyId(request.recipientCompanyId())
                .supplyCompanyId(supplyCompanyId)
                .productId(request.productId())
                .quantity(request.quantity())
                .price(BigDecimal.valueOf(request.price()))
                .requestNotes(request.requestNote())
                .build();
    }

    /**
     * 주문 총 가격 계산하는 메서드
     *
     * @return 주문 총 가격
     */
    public int calculateTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity)).intValue();
    }

    /**
     * 주문 정보 수정하는 메서드
     *
     * @param request 주문 수정 정보
     */
    public void update(OrderUpdateRequest request) {
        this.quantity = request.quantity();
        this.requestNotes = request.requestNote();
    }

    /**
     * 주문의 상태를 수정하는 메서드
     *
     * @param status 주문 상태
     */
    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * 주문의 배송 정보를 추가하는 메서드
     *
     * @param deliveryId 배송 ID
     */
    public void updateDelivery(UUID deliveryId) {

    }

    /**
     * 주문 삭제 (soft delete)
     */
    public void delete() {
        this.isDelete = true;
    }
}
