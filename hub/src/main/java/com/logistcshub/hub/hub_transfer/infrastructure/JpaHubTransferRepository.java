package com.logistcshub.hub.hub_transfer.infrastructure;


import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaHubTransferRepository extends JpaRepository<HubTransfer, UUID> {
    boolean existsByStartHubAndEndHubAndIsDeletedFalse(Hub startHub, Hub endHub);

    @Query("select ht from HubTransfer ht join fetch ht.startHub join fetch ht.endHub where ht.id = :id and ht.isDeleted is false ")
    Optional<HubTransfer> findByIdAndIsDeletedFalse(UUID id);
}
