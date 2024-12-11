package com.logistcshub.hub.hub.infrastructure;

import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub.domain.repository.HubRepository;
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
}
