package com.logistics.delivery.infrastructure.repository;

import com.logistics.delivery.domain.model.DeliveryRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaDeliveryRouteRepository extends JpaRepository<DeliveryRoute, UUID> {

    @Query("SELECT dr FROM DeliveryRoute dr WHERE dr.status = 'PENDING' AND dr.isDelete IS FALSE")
    List<DeliveryRoute> findPendingRoutes();

    @Query("SELECT dr.deliveryManagerId FROM DeliveryRoute dr " +
            "WHERE dr.startHubId = :startHubId AND dr.endHubId = :endHubId " +
            "AND dr.status = 'ASSIGNED'")
    Optional<Long> findAssignedManagerByRoute(UUID startHubId, UUID endHubId);

    @Query("SELECT DISTINCT dr.deliveryManagerId FROM DeliveryRoute dr WHERE dr.status = 'ASSIGNED'")
    List<Long> findAssignedManagerIds();
}
