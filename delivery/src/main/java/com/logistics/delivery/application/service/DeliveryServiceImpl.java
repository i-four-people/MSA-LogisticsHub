package com.logistics.delivery.application.service;

import com.logistics.delivery.application.common.OrderStatus;
import com.logistics.delivery.application.dto.company.CompanyResponse;
import com.logistics.delivery.application.dto.event.DeliveryCreateEvent;
import com.logistics.delivery.application.dto.event.consume.OrderCreateConsume;
import com.logistics.delivery.application.dto.order.OrderStatusRequest;
import com.logistics.delivery.domain.model.Delivery;
import com.logistics.delivery.domain.model.DeliveryStatus;
import com.logistics.delivery.domain.repository.DeliveryRepository;
import com.logistics.delivery.domain.service.DeliveryRouteService;
import com.logistics.delivery.domain.service.DeliveryService;
import com.logistics.delivery.infrastructure.client.CompanyClient;
import com.logistics.delivery.infrastructure.config.RabbitMQProperties;
import com.logistics.delivery.presentation.exception.BusinessException;
import com.logistics.delivery.presentation.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryRouteService deliveryRouteService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitProperties;

    private final CompanyClient companyClient;

    @Override
    public boolean isOrderStatusChangeAllowed(UUID deliveryId, OrderStatusRequest request) {

        Delivery findDelivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new BusinessException(ErrorCode.DELIVERY_NOT_FOUND)
        );

        OrderStatus newOrderStatus = request.orderStatus();
        DeliveryStatus deliveryStatus = findDelivery.getStatus();

        return switch (deliveryStatus) {
            case PENDING -> true;
            case IN_TRANSIT -> false; // 배송 중에는 주문 상태 변경 불가
            case AT_HUB, CANCELLED -> newOrderStatus == OrderStatus.CANCELED;
            case OUT_FOR_DELIVERY -> false; // 업체에 배숭 중일 때는 주문 상태 변경 불가
            case DELIVERED -> newOrderStatus == OrderStatus.COMPLETED;
        };

    }

    @Override
    public void createDelivery(OrderCreateConsume consume) {

        // 출발 허브, 도착 허브 조회
        CompanyResponse recipientCompany = companyClient.findCompanyById(consume.recipientCompanyId()); // 수령 업체
        CompanyResponse supplyCompany = companyClient.findCompanyById(consume.supplyCompanyId()); // 공급 업체

        // 주문의 배송이 이미 존재하는 경우
        boolean deliveryExists = deliveryRepository.existsByOrderId(consume.orderId());
        if (deliveryExists) {
            throw new BusinessException(ErrorCode.DUPLICATE_DELIVERY);
        }

        // 배송 정보 저장
        Delivery savedDelivery = deliveryRepository.save(Delivery.create(consume, recipientCompany, supplyCompany));

        // 배송 경로 생성
        deliveryRouteService.createRoutesForDelivery(savedDelivery);

        // 이벤트 생성
        DeliveryCreateEvent event = DeliveryCreateEvent.of(savedDelivery);

        // 이벤트 발행
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getDelivery(),
                event
        );

    }

}
