package com.logistcshub.hub.hub_transfer.domain.repository;

import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub_transfer.application.dtos.HubTransferPageDto;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface HubTransferRepository {
    HubTransfer save(HubTransfer hubTransfer);

    boolean existsByStartHubAndEndHubAndDeletedFalse(Hub startHub, Hub endHub);

    HubTransferPageDto findAll(List<UUID> idList, Predicate predicate, Pageable pageable);

    List<HubTransfer> saveAll(List<HubTransfer> saveList);
}
