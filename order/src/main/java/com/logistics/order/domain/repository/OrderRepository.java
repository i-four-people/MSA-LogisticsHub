package com.logistics.order.domain.repository;

import com.logistics.order.application.dto.SearchParameter;
import com.logistics.order.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Optional<Order> findById(UUID id);

    List<Order> findAll();

    Order save(Order order);

    void deleteById(UUID id);

    Page<Order> searchOrders(SearchParameter searchParameter);

    Page<Order> searchOrdersByCompanyIds(SearchParameter searchParameter, List<UUID> companyIds);
}
