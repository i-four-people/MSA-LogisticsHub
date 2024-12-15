package com.logistcshub.hub.hub_transfer.domain.repository;

import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;

import java.util.Optional;
import java.util.UUID;

public interface HubTransferRepository {
    HubTransfer save(HubTransfer hubTransfer);

    boolean existsByStartHubAndEndHubAndIsDeletedFalse(Hub startHub, Hub endHub);

    Optional<HubTransfer> findByIdAndIsDeletedFalse(UUID id);

    void delete(HubTransfer hubTransfer);
}
