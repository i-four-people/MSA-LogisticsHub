package com.logistcshub.hub.area.domain.repository;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.area.domain.model.type.City;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AreaRepository {
    Area save(Area area);

    List<Area> saveAll(List<Area> areas);

    Optional<Area> findById(UUID id);

    boolean existsByCity(City city);
}
