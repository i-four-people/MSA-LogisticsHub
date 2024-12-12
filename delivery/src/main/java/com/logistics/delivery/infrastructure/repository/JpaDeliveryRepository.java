package com.logistics.delivery.infrastructure.repository;

import com.logistics.delivery.domain.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaDeliveryRepository extends JpaRepository<Delivery, UUID> {
}
