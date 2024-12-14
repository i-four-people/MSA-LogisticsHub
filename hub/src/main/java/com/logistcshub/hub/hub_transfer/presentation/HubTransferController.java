package com.logistcshub.hub.hub_transfer.presentation;

import static com.logistcshub.hub.common.domain.model.type.ResponseMessage.SUCCESS_CREATE_HUB_TRANSFER;
import static com.logistcshub.hub.common.domain.model.type.ResponseMessage.SUCCESS_SEARCH_HUB_TRANSFERS;

import com.logistcshub.hub.common.domain.model.dtos.SuccessResponse;
import com.logistcshub.hub.hub_transfer.application.dtos.AddHubTransferResponseDto;
import com.logistcshub.hub.hub_transfer.application.dtos.HubTransferPageDto;
import com.logistcshub.hub.hub_transfer.application.service.HubTransferService;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;
import com.logistcshub.hub.hub_transfer.presentation.request.AddHubTransferRequestDto;
import java.util.List;
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

    @GetMapping
    public ResponseEntity<SuccessResponse<HubTransferPageDto>> searchHubTransfer(@RequestParam(required = false) List<UUID> idList,
                                                                                 @QuerydslPredicate(root = HubTransfer.class) Predicate predicate,
                                                                                 @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                                 @RequestHeader(value = "X-USER-ID") Long userId,
                                                                                 @RequestHeader(value = "X-USER-ROLE") String role) {
        userId = 1L;
        role = "MASTER";

        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_SEARCH_HUB_TRANSFERS, hubTransferService.searchHubTransfer(idList, predicate, pageable, role, userId))
        );
    }
}
