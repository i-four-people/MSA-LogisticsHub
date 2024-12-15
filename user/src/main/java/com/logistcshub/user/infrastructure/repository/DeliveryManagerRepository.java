package com.logistcshub.user.infrastructure.repository;

import com.logistcshub.user.domain.model.deliveryManager.DeliveryManager;
import com.logistcshub.user.domain.repository.DeliveryManagerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryManagerRepository extends JpaRepository<DeliveryManager, Long>, DeliveryManagerRepositoryCustom {
}
