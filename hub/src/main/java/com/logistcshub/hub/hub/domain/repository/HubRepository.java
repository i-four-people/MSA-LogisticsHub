package com.logistcshub.hub.hub.domain.repository;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.hub.domain.mode.Hub;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRepository {

    Hub save(Hub hub);

    Optional<Hub> findByIdAndDeletedFalse(UUID id);

    Optional<Hub> findByIdWithAreaAndDeletedFalse(UUID id);

    boolean existsByAreaAndAddressAndDeletedFalse(Area area, String address);

    List<Hub> findAll();
}
