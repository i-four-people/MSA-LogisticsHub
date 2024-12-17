package com.logistics.delivery.domain.service;

import com.logistics.delivery.application.dto.PageResponse;
import com.logistics.delivery.application.dto.SearchParameter;
import com.logistics.delivery.application.dto.delivery.DeliveryDeleteResponse;
import com.logistics.delivery.application.dto.delivery.DeliveryDetailResponse;
import com.logistics.delivery.application.dto.delivery.DeliveryResponse;
import com.logistics.delivery.application.dto.event.consume.OrderCreateConsume;
import com.logistics.delivery.application.dto.order.OrderStatusRequest;
import com.logistics.delivery.domain.model.Delivery;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {

    boolean isOrderStatusChangeAllowed(UUID deliveryId, OrderStatusRequest request);

    void createDelivery(OrderCreateConsume delivery);

    void assignCompanyDeliveryManager(Delivery delivery);

    List<Delivery> findUnassignedDeliveries();

    PageResponse<DeliveryResponse> getDeliveries(SearchParameter searchParameter);

    DeliveryDetailResponse getDeliveryById(UUID deliveryId);

    DeliveryDeleteResponse deleteDeliveryById(UUID deliveryId);
}
