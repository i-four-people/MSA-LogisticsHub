package com.logistics.delivery.domain.service;

import com.logistics.delivery.application.dto.order.OrderStatusRequest;

import java.util.UUID;

public interface DeliveryService {

    boolean isOrderStatusChangeAllowed(UUID deliveryId, OrderStatusRequest request);
}
