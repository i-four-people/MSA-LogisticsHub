package com.logistics.order.domain.repository;

import com.logistics.order.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Optional<Order> findById(UUID id);

    List<Order> findAll();

    Order save(Order order);

    void deleteById(UUID id);

}
