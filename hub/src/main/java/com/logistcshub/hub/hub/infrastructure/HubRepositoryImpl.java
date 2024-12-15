package com.logistcshub.hub.hub.infrastructure;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub.domain.repository.HubRepository;

import java.util.List;
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
    public Optional<Hub> findByIdAndDeletedFalse(UUID id) {
        return jpaHubRepository.findByIdAndIsDeletedFalse(id);
    }

    @Override
    public Optional<Hub> findByIdWithAreaAndDeletedFalse(UUID id) {
        return jpaHubRepository.findByIdWithAreaAndDeletedFalse(id);
    }

    @Override
    public boolean existsByAreaAndAddressAndDeletedFalse(Area area, String address) {
        return jpaHubRepository.existsByAreaAndAddressAndIsDeletedFalse(area, address);
    }

    @Override
    public List<Hub> findAll() {
        return jpaHubRepository.findByIsDeletedFalse();
    }
}
