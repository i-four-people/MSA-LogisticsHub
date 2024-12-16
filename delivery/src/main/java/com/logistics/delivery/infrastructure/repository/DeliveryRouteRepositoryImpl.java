package com.logistics.delivery.infrastructure.repository;

import com.logistics.delivery.domain.model.DeliveryRoute;
import com.logistics.delivery.domain.repository.DeliveryRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryRouteRepositoryImpl implements DeliveryRouteRepository {

    private final JpaDeliveryRouteRepository jpaDeliveryRouteRepository;

    @Override
    public Optional<DeliveryRoute> findById(UUID id) {
        return jpaDeliveryRouteRepository.findById(id);
    }

    @Override
    public List<DeliveryRoute> findAll() {
        return jpaDeliveryRouteRepository.findAll();
    }

    @Override
    public DeliveryRoute save(DeliveryRoute deliveryRoute) {
        return jpaDeliveryRouteRepository.save(deliveryRoute);
    }

    @Override
    public void deleteById(UUID id) {
        jpaDeliveryRouteRepository.deleteById(id);
    }

    @Override
    public List<DeliveryRoute> findPendingRoutes() {
        return jpaDeliveryRouteRepository.findPendingRoutes();
    }

    @Override
    public Optional<UUID> findAssignedManagerByRoute(UUID startHubId, UUID endHubId) {
        return jpaDeliveryRouteRepository.findAssignedManagerByRoute(startHubId, endHubId);
    }

    @Override
    public List<UUID> findAssignedManagerIds() {
        return jpaDeliveryRouteRepository.findAssignedManagerIds();
    }
}
