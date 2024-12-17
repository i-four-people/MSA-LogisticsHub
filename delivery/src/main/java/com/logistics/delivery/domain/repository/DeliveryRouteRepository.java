package com.logistics.delivery.domain.repository;

import com.logistics.delivery.domain.model.DeliveryRoute;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRouteRepository {

    Optional<DeliveryRoute> findById(UUID id);

    List<DeliveryRoute> findAll();

    DeliveryRoute save(DeliveryRoute delivery);

    void deleteById(UUID id);

    List<DeliveryRoute> findPendingRoutes();

    Optional<Long> findAssignedManagerByRoute(UUID startHubId, UUID endHubId);

    List<Long> findAssignedManagerIds();

    Optional<DeliveryRoute> findByDeliveryId(UUID id);

    List<DeliveryRoute> findByDeliveryId(UUID deliveryId);
}
