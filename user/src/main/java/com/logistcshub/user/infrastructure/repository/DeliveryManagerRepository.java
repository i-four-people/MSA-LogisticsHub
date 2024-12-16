package com.logistcshub.user.infrastructure.repository;

import com.logistcshub.user.domain.model.deliveryManager.DeliveryManager;
import com.logistcshub.user.domain.model.deliveryManager.DeliveryManagerType;
import com.logistcshub.user.domain.repository.DeliveryManagerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryManagerRepository extends JpaRepository<DeliveryManager, Long>, DeliveryManagerRepositoryCustom {

    List<DeliveryManager> findByDeliveryManagerTypeAndDeletedAtIsNull(DeliveryManagerType type);
}
