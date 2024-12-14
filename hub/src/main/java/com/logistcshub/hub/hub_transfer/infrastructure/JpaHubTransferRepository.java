package com.logistcshub.hub.hub_transfer.infrastructure;


import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaHubTransferRepository extends JpaRepository<HubTransfer, UUID> {
    boolean existsByStartHubAndEndHubAndIsDeletedFalse(Hub startHub, Hub endHub);
}
