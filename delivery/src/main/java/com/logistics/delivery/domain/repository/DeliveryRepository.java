package com.logistics.delivery.domain.repository;

import com.logistics.delivery.domain.model.Delivery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository {

    Optional<Delivery> findById(UUID id);

    List<Delivery> findByOrderId(UUID orderId);

    boolean existsByOrderId(UUID orderId);

    List<Delivery> findAll();

    Delivery save(Delivery delivery);

    void deleteById(UUID id);
}
