package com.logistcshub.hub.hub_transfer.infrastructure;

import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;
import com.logistcshub.hub.hub_transfer.domain.repository.HubTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HubTransferRepositoryImpl implements HubTransferRepository {

    private final JpaHubTransferRepository jpaHubTransferRepository;

    @Override
    public HubTransfer save(HubTransfer hubTransfer) {
        return jpaHubTransferRepository.save(hubTransfer);
    }

    @Override
    public boolean existsByStartHubAndEndHubAndIsDeletedFalse(Hub startHub, Hub endHub) {
        return jpaHubTransferRepository.existsByStartHubAndEndHubAndIsDeletedFalse(startHub, endHub);
    }

    @Override
    public Optional<HubTransfer> findByIdAndIsDeletedFalse(UUID id) {
        return jpaHubTransferRepository.findByIdAndIsDeletedFalse(id);
    }

    @Override
    public void delete(HubTransfer hubTransfer) {
        jpaHubTransferRepository.delete(hubTransfer);
    }
}
