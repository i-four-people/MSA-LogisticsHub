package com.logistcshub.hub.hub_transfer.presentation;

import static com.logistcshub.hub.common.domain.model.type.ResponseMessage.SUCCESS_CREATE_HUB_TRANSFER;

import com.logistcshub.hub.common.domain.model.dtos.SuccessResponse;
import com.logistcshub.hub.hub_transfer.application.dtos.AddHubTransferResponseDto;
import com.logistcshub.hub.hub_transfer.application.service.HubTransferService;
import com.logistcshub.hub.hub_transfer.presentation.request.AddHubTransferRequestDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/hub-transfers")
@RestController
@RequiredArgsConstructor
public class HubTransferController {
    private final HubTransferService hubTransferService;

    @PostMapping
    public ResponseEntity<SuccessResponse<List<AddHubTransferResponseDto>>> addHubTransfer(@RequestBody AddHubTransferRequestDto request,
                                                                                          @RequestHeader(value = "X-USER-ID") Long userId,
                                                                                          @RequestHeader(value = "X-USER-ROLE") String role) {
        userId = 1L;
        role = "MASTER";

        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_CREATE_HUB_TRANSFER, hubTransferService.addHubTransfer(request, role, userId))
        );
    }
}
