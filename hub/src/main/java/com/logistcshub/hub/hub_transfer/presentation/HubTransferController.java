package com.logistcshub.hub.hub_transfer.presentation;

import com.logistcshub.hub.common.domain.model.dtos.SuccessResponse;
import com.logistcshub.hub.hub_transfer.application.dtos.AddHubTransferResponseDto;
import com.logistcshub.hub.hub_transfer.application.dtos.DeleteHubTransferResponseDto;
import com.logistcshub.hub.hub_transfer.application.dtos.HubTransferResponseDto;
import com.logistcshub.hub.hub_transfer.application.dtos.UpdateHubTransferResponseDto;
import com.logistcshub.hub.hub_transfer.application.service.HubTransferService;
import com.logistcshub.hub.hub_transfer.presentation.request.AddHubTransferRequestDto;
import java.util.List;
import java.util.UUID;

import com.logistcshub.hub.hub_transfer.presentation.request.UpdateTransferRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.logistcshub.hub.common.domain.model.type.ResponseMessage.*;

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

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<UpdateHubTransferResponseDto>> updateHubTransfer(@PathVariable UUID id,
                                                                                            @RequestBody UpdateTransferRequestDto request,
                                                                                            @RequestHeader(value = "X-USER-ID") Long userId,
                                                                                            @RequestHeader(value = "X-USER-ROLE") String role) {
        userId = 1L;
        role = "MASTER";

        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_UPDATE_HUB_TRANSFER, hubTransferService.updateTransfer(id, request, role, userId))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<DeleteHubTransferResponseDto>> deleteHubTransfer(@PathVariable UUID id,
                                                                                           @RequestHeader(value = "X-USER-ID") Long userId,
                                                                                           @RequestHeader(value = "X-USER-ROLE") String role) {
        userId = 1L;
        role = "MASTER";

        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_DELETE_HUB_TRANSFER, hubTransferService.deleteHubTransfer(id, role, userId))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<HubTransferResponseDto>> getHubTransfer(@PathVariable UUID id,
                                                                                  @RequestHeader(value = "X-USER-ID") Long userId,
                                                                                  @RequestHeader(value = "X-USER-ROLE") String role) {
        userId = 1L;
        role = "MASTER";

        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_GET_HUB_TRANSFER, hubTransferService.getHubTransfer(id, role, userId))
        );
    }
}
