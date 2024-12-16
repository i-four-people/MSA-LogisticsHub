package com.logistics.delivery.infrastructure.repository;

import com.logistics.delivery.domain.model.DeliveryRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaDeliveryRouteRepository extends JpaRepository<DeliveryRoute, UUID> {
}
