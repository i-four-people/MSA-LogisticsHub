package com.logistcshub.user.infrastructure.repository;

import com.logistcshub.user.domain.model.deliveryManager.HubManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HubManagerRepository extends JpaRepository<HubManager, UUID> {
    Optional<HubManager> findByUserId(Long userId);
}
