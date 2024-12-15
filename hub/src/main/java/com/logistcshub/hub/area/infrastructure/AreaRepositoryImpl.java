package com.logistcshub.hub.area.infrastructure;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.area.domain.model.type.City;
import com.logistcshub.hub.area.domain.model.type.State;
import com.logistcshub.hub.area.domain.repository.AreaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class AreaRepositoryImpl implements AreaRepository {
    private final JpaAreaRepository jpaAreaRepository;

    @Override
    public Area save(Area area) {
        return jpaAreaRepository.save(area);
    }

    @Override
    public List<Area> saveAll(List<Area> areas) {
        return jpaAreaRepository.saveAll(areas);
    }

    @Override
    public Optional<Area> findById(UUID id) {
        return jpaAreaRepository.findByIdAndIsDeletedFalse(id);
    }

    @Override
    public Optional<Area> findByStateAndCity(State state, City city) {
        return jpaAreaRepository.findByStateAndCityAndIsDeletedFalse(state, city);
    }

    @Override
    public boolean existsByCity(City city) {
        return jpaAreaRepository.existsByCityAndIsDeletedFalse(city);
    }
}
