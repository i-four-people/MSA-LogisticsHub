package com.logistcshub.hub.hub.domain.repository;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.hub.application.dtos.HubResponseDto;
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

    Optional<Hub> findByAreaAndIsDeletedFalse(UUID areaId, double lat, double lng);

    Optional<Hub> findByAreaInAndIsDeletedFalse(List<UUID> areaList, double lat, double lng);

    List<HubResponseDto> findByIdInAndIsDeletedFalse(List<UUID> idList);

    List<Hub> findByIsDeletedFalse();
}
