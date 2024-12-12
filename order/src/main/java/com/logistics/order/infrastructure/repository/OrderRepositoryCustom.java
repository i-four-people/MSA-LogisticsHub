package com.logistics.order.infrastructure.repository;

import com.logistics.order.domain.model.Order;
import com.logistics.order.presentation.request.SearchParameter;
import org.springframework.data.domain.Page;

public interface OrderRepositoryCustom {

    Page<Order> searchOrders(SearchParameter searchParameter);
}
