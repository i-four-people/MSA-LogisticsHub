package com.logistics.delivery.application.service;

import com.logistics.delivery.application.common.OrderStatus;
import com.logistics.delivery.application.dto.order.OrderStatusRequest;
import com.logistics.delivery.domain.model.Delivery;
import com.logistics.delivery.domain.model.DeliveryStatus;
import com.logistics.delivery.domain.repository.DeliveryRepository;
import com.logistics.delivery.domain.service.DeliveryService;
import com.logistics.delivery.presentation.exception.BusinessException;
import com.logistics.delivery.presentation.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

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

}
