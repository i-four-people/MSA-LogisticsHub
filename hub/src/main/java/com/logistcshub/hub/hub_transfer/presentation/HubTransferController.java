package com.logistcshub.hub.hub_transfer.presentation;

import com.logistcshub.hub.hub_transfer.application.service.HubTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/hub-transfers")
@RestController
@RequiredArgsConstructor
public class HubTransferController {
    private final HubTransferService hubTransferService;
}
