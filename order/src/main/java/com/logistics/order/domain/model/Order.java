package com.logistics.order.domain.model;

import com.logistics.order.application.dto.OrderCreateRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Comment("요청 업체(공급 업체) ID")
    private UUID requesterCompanyId;

    @Comment("수령 업체 ID")
    private UUID recipientCompanyId;

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

    @Comment("비활성화 여부")
    private boolean isDelete;

    @Builder
    private Order(UUID requesterCompanyId, UUID recipientCompanyId, UUID deliveryId, UUID productId, BigDecimal price, int quantity, String requestNotes) {
        this.requesterCompanyId = requesterCompanyId;
        this.recipientCompanyId = recipientCompanyId;
        this.deliveryId = deliveryId;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.requestNotes = requestNotes;
    }

    /**
     * 주문 생성하는 정적 팩토리 메서드
     *
     * @param request 주문 생성 request
     * @return Order
     */
    public static Order create(OrderCreateRequest request) {
        return Order.builder()
                .recipientCompanyId(request.recipientCompanyId())
                .productId(request.productId())
                .quantity(request.quantity())
                .price(BigDecimal.valueOf(request.price()))
                .requestNotes(request.requestNote())
                .build();
    }

    // 총 가격 계산 메서드
    public int calculateTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity)).intValue();
    }

}
