package com.logistcshub.hub.hub_transfer.presentation;

import static com.logistcshub.hub.common.domain.model.type.ResponseMessage.SUCCESS_CREATE_HUB_TRANSFER;
import static com.logistcshub.hub.common.domain.model.type.ResponseMessage.SUCCESS_SEARCH_HUB_TRANSFERS;

import com.logistcshub.hub.common.domain.model.dtos.SuccessResponse;
import com.logistcshub.hub.hub_transfer.application.dtos.*;
import com.logistcshub.hub.hub_transfer.application.service.HubTransferService;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;
import com.logistcshub.hub.hub_transfer.presentation.request.AddHubTransferRequestDto;
import java.util.List;
import java.util.UUID;

import com.logistcshub.hub.hub_transfer.presentation.request.UpdateTransferRequestDto;
import java.util.UUID;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
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
                                                                                           @RequestHeader(value = "X-User-Id") Long userId,
                                                                                           @RequestHeader(value = "X-User-Role") String role) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_CREATE_HUB_TRANSFER, hubTransferService.addHubTransfer(request, role, userId))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<UpdateHubTransferResponseDto>> updateHubTransfer(@PathVariable UUID id,
                                                                                            @RequestBody UpdateTransferRequestDto request,
                                                                                           @RequestHeader(value = "X-User-Id") Long userId,
                                                                                           @RequestHeader(value = "X-User-Role") String role) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_UPDATE_HUB_TRANSFER, hubTransferService.updateTransfer(id, request, role, userId))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<DeleteHubTransferResponseDto>> deleteHubTransfer(@PathVariable UUID id,
                                                                                           @RequestHeader(value = "X-User-Id") Long userId,
                                                                                           @RequestHeader(value = "X-User-Role") String role) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_DELETE_HUB_TRANSFER, hubTransferService.deleteHubTransfer(id, role, userId))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<HubTransferResponseDto>> getHubTransfer(@PathVariable UUID id,
                                                                                  @RequestHeader(value = "X-User-Id") Long userId,
                                                                                  @RequestHeader(value = "X-User-Role") String role) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_GET_HUB_TRANSFER, hubTransferService.getHubTransfer(id, role, userId))
        );
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<HubTransferPageDto>> searchHubTransfer(@RequestParam(required = false) List<UUID> idList,
                                                                                 @QuerydslPredicate(root = HubTransfer.class) Predicate predicate,
                                                                                 @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                                 @RequestHeader(value = "X-User-Id") Long userId,
                                                                                 @RequestHeader(value = "X-User-Role") String role) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_SEARCH_HUB_TRANSFERS, hubTransferService.searchHubTransfer(idList, predicate, pageable, role, userId))
        );
    }

    @GetMapping("/hub-to-hub")
    public ResponseEntity<SuccessResponse<HubToHubResponseDto>> getAllHubTransfers(@RequestParam(required = true) UUID startHubId,
                                                                                   @RequestParam(required = true) UUID endHubId,
                                                                                   @RequestHeader(value = "X-User-Id") Long userId,
                                                                                   @RequestHeader(value = "X-User-Role") String role) {

        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_SEARCH_HUB_TRANSFER, hubTransferService.getHubToHub(startHubId,endHubId, role, userId))
        );
    }
}
