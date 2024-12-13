package com.logistcshub.hub.hub_transfer.domain.repository;

import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;

public interface HubTransferRepository {
    HubTransfer save(HubTransfer hubTransfer);

    boolean existsByStartHubAndEndHubAndDeletedFalse(Hub startHub, Hub endHub);
}
