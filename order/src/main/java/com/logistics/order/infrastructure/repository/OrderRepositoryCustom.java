package com.logistics.order.infrastructure.repository;

import com.logistics.order.application.dto.SearchParameter;
import com.logistics.order.domain.model.Order;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface OrderRepositoryCustom {

    Page<Order> searchOrders(SearchParameter searchParameter);

    Page<Order> searchOrdersByCompanyIds(SearchParameter searchParameter, List<UUID> companyIds);
}
