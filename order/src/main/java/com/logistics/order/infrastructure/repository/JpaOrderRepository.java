package com.logistics.order.infrastructure.repository;

import com.logistics.order.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<Order, UUID>, OrderRepositoryCustom {

    Optional<Order> findByIdAndIsDeleteFalse(UUID id);
}
