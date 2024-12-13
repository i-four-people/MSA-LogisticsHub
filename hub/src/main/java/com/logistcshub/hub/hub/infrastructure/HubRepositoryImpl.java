package com.logistcshub.hub.hub.infrastructure;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub.domain.repository.HubRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepository {
    private final JpaHubRepository jpaHubRepository;


    @Override
    public Hub save(Hub hub) {
        return jpaHubRepository.save(hub);
    }

    @Override
    public Optional<Hub> findById(UUID id) {
        return jpaHubRepository.findById(id);
    }

    @Override
    public Optional<Hub> findByIdWithArea(UUID id) {
        return jpaHubRepository.findByIdWithArea(id);
    }

    @Override
    public boolean existsByAreaAndAddress(Area area, String address) {
        return jpaHubRepository.existsByAreaAndAddress(area, address);
    }
}
