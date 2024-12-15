package com.logistics.delivery.domain.service;

import com.logistics.delivery.application.dto.event.consume.OrderCreateConsume;
import com.logistics.delivery.application.dto.order.OrderStatusRequest;

import java.util.UUID;

public interface DeliveryService {

    boolean isOrderStatusChangeAllowed(UUID deliveryId, OrderStatusRequest request);

    void createDelivery(OrderCreateConsume delivery);
}
