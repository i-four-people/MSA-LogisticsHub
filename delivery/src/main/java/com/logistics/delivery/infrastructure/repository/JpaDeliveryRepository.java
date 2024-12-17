package com.logistics.delivery.infrastructure.repository;

import com.logistics.delivery.domain.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaDeliveryRepository extends JpaRepository<Delivery, UUID> {
    List<Delivery> findByOrderIdAndIsDeletedFalseOrderByCreatedAt(UUID orderId);
    boolean existsByOrderId(UUID orderId);
}
