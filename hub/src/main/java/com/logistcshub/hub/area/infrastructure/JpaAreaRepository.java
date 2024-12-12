package com.logistcshub.hub.area.infrastructure;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.area.domain.model.type.City;
import com.logistcshub.hub.area.domain.repository.AreaRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAreaRepository extends JpaRepository<Area, UUID> {
    boolean existsByCity(City city);
}
