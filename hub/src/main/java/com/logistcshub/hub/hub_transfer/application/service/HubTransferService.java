package com.logistcshub.hub.hub_transfer.application.service;

import com.logistcshub.hub.hub_transfer.domain.repository.HubTransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubTransferService {
    private final HubTransferRepository hubTransferRepository;
}
