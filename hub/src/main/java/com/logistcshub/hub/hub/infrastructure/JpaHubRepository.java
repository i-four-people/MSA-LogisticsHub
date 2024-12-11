package com.logistcshub.hub.hub.infrastructure;

import com.logistcshub.hub.hub.domain.mode.Hub;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaHubRepository extends JpaRepository<Hub, UUID> {
}
