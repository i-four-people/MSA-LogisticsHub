package com.logistics.delivery.domain.service;

import com.logistics.delivery.domain.model.Delivery;

public interface DeliveryRouteService {

    void createRoutesForDelivery(Delivery delivery);
}
