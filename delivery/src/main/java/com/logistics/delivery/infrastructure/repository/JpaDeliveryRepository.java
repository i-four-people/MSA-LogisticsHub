package com.logistics.delivery.infrastructure.repository;

import com.logistics.delivery.domain.model.Delivery;
import com.logistics.delivery.domain.model.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaDeliveryRepository extends JpaRepository<Delivery, UUID>, DeliveryRepositoryCustom {

    Optional<Delivery> findByOrderId(UUID orderId);

    boolean existsByOrderId(UUID orderId);

    @Query("SELECT d FROM Delivery d " +
            "WHERE d.destinationHubId = :destinationHubId " +
            "AND d.status NOT IN :statusList " +
            "AND d.isDelete IS FALSE ")
    List<Delivery> findActiveDeliveriesByDestinationHubId(@Param("destinationHubId") UUID destinationHubId,
                                                          @Param("statusList") List<DeliveryStatus> statusList);

    @Query("SELECT d FROM Delivery d " +
            "WHERE d.companyDeliveryManagerId IS NULL " +
            "AND d.status NOT IN :statusList " +
            "AND d.isDelete IS FALSE ")
    List<Delivery> findByCompanyDeliveryManagerIdIsNull(@Param("statusList") List<DeliveryStatus> statusList);

    List<Delivery> findByOrderIdAndIsDeletedFalseOrderByCreatedAt(UUID orderId);

}
