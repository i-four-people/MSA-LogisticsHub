package com.logistics.delivery.infrastructure.repository;

import com.logistics.delivery.application.dto.SearchParameter;
import com.logistics.delivery.domain.model.Delivery;
import com.logistics.delivery.domain.model.DeliveryStatus;
import com.logistics.delivery.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final JpaDeliveryRepository jpaDeliveryRepository;

    @Override
    public Page<Delivery> searchDeliveries(SearchParameter searchParameter) {
        return jpaDeliveryRepository.searchDeliveries(searchParameter);
    }

    @Override
    public Optional<Delivery> findById(UUID id) {
        return jpaDeliveryRepository.findById(id);
    }

    @Override
    public Optional<Delivery> findByOrderId(UUID orderId) {
        return jpaDeliveryRepository.findByOrderId(orderId);
    }

    @Override
    public boolean existsByOrderId(UUID orderId) {
        return jpaDeliveryRepository.existsByOrderId(orderId);
    }

    @Override
    public List<Delivery> findAll() {
        return jpaDeliveryRepository.findAll();
    }

    @Override
    public Delivery save(Delivery delivery) {
        return jpaDeliveryRepository.save(delivery);
    }

    @Override
    public void deleteById(UUID id) {
        jpaDeliveryRepository.deleteById(id);
    }

    @Override
    public List<Delivery> findActiveDeliveriesByDestinationHubId(UUID destinationHubId, List<DeliveryStatus> statusList) {
        return jpaDeliveryRepository.findActiveDeliveriesByDestinationHubId(destinationHubId, statusList);
    }

    @Override
    public List<Delivery> findUnassignedDeliveries() {
        return jpaDeliveryRepository.findByCompanyDeliveryManagerIdIsNull(List.of(DeliveryStatus.DELIVERED, DeliveryStatus.CANCELLED));
    }

    @Override
    public List<Delivery> findAllByStatusNotIn(List<DeliveryStatus> statusList) {
        return jpaDeliveryRepository.findAllByStatusNotIn(statusList);
    }
}
