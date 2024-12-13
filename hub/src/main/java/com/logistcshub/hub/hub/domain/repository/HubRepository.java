package com.logistcshub.hub.hub.domain.repository;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.hub.domain.mode.Hub;
import java.util.Optional;
import java.util.UUID;

public interface HubRepository {

    Hub save(Hub hub);

    Optional<Hub> findById(UUID id);

    Optional<Hub> findByIdWithArea(UUID id);

    boolean existsByAreaAndAddress(Area area, String address);
}
