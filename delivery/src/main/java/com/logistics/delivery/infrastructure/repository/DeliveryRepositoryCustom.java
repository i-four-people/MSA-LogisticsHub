package com.logistics.delivery.infrastructure.repository;

import com.logistics.delivery.application.dto.SearchParameter;
import com.logistics.delivery.domain.model.Delivery;
import org.springframework.data.domain.Page;

public interface DeliveryRepositoryCustom {

    Page<Delivery> searchDeliveries(SearchParameter searchParameter);
}
