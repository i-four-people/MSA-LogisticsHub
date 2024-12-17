package com.logistcshub.hub.area.infrastructure;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.area.domain.model.type.City;
import com.logistcshub.hub.area.domain.model.type.State;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAreaRepository extends JpaRepository<Area, UUID> {
    boolean existsByCityAndIsDeletedFalse(City city);

    Optional<Area> findByStateAndCityAndIsDeletedFalse(State state, City city);
    Optional<Area> findByIdAndIsDeletedFalse(UUID id);

    List<Area> findByStateAndIsDeletedFalse(State state);
}
