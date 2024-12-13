package com.logistcshub.user.infrastructure.repository;

import com.logistcshub.user.domain.model.DeliveryManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryManagerRepository extends JpaRepository<DeliveryManager, Long> {
}
