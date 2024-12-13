package com.logistcshub.hub.hub_transfer.infrastructure;


import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaHubTransferRepository extends JpaRepository<HubTransfer, UUID> {
}
