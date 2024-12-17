package com.logistics.order.infrastructure.repository;

import com.logistics.order.application.dto.SearchParameter;
import com.logistics.order.domain.model.Order;
import com.logistics.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaOrderRepository.findByIdAndIsDeleteFalse(id);
    }

    @Override
    public List<Order> findAll() {
        return jpaOrderRepository.findAll();
    }

    @Override
    public Order save(Order order) {
        return jpaOrderRepository.save(order);
    }

    @Override
    public void deleteById(UUID id) {
        jpaOrderRepository.deleteById(id);
    }

    @Override
    public Page<Order> searchOrders(SearchParameter searchParameter) {
        return jpaOrderRepository.searchOrders(searchParameter);
    }

    @Override
    public Page<Order> searchOrdersByCompanyIds(SearchParameter searchParameter, List<UUID> companyIds) {
        return jpaOrderRepository.searchOrdersByCompanyIds(searchParameter, companyIds);
    }

}
