package com.logistcshub.hub.hub.presentation;

import com.logistcshub.hub.common.domain.model.dtos.SuccessResponse;
import com.logistcshub.hub.hub.application.dtos.AddHubResponseDto;
import com.logistcshub.hub.hub.application.dtos.DeleteHubResponseDto;
import com.logistcshub.hub.hub.application.dtos.HubResponseDto;
import com.logistcshub.hub.hub.application.dtos.UpdateHubResponseDto;
import com.logistcshub.hub.hub.application.service.HubService;
import com.logistcshub.hub.hub.presentation.request.AddHubRequestDto;
import com.logistcshub.hub.hub.presentation.request.HubIdsDto;
import com.logistcshub.hub.hub.presentation.request.UpdateHubRequestDto;
import com.logistcshub.hub.hub.presentation.request.type.HubSearchType;
import com.logistcshub.hub.hub.presentation.request.type.SortType;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.logistcshub.hub.common.domain.model.type.ResponseMessage.*;

@RestController
@RequestMapping("/api/hubs")
@RequiredArgsConstructor
public class HubController {

    private final HubService hubService;

    @PostMapping
    public ResponseEntity<SuccessResponse<AddHubResponseDto>> addHub(@RequestBody AddHubRequestDto request,
                                                                     @RequestHeader(value = "X-User-Id") Long userId,
                                                                     @RequestHeader(value = "X-User-Role") String role) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_CREATE_HUB, hubService.addHub(userId, role, request))
        );

    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<UpdateHubResponseDto>> updateHub(@RequestBody UpdateHubRequestDto request,
                                                                           @PathVariable UUID id,
                                                                           @RequestHeader(value = "X-User-Id") Long userId,
                                                                           @RequestHeader(value = "X-User-Role") String role) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_UPDATE_HUB, hubService.updateHub(id, userId, role, request))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<DeleteHubResponseDto>> deleteHub(@PathVariable UUID id,
                                                                           @RequestHeader(value = "X-User-Id") Long userId,
                                                                           @RequestHeader(value = "X-User-Role") String role) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_DELETE_HUB, hubService.deleteHub(id, userId, role))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<HubResponseDto>> getHub(@PathVariable UUID id,
                                                                  @RequestHeader(value = "X-User-Id") Long userId,
                                                                  @RequestHeader(value = "X-User-Role") String role) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_GET_HUB, hubService.getHub(id, userId, role))
        );
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<PagedModel<HubResponseDto>>> searchHubs(
                                                                            @RequestHeader(value = "X-User-Id") Long userId,
                                                                            @RequestHeader(value = "X-User-Role") String role,
                                                                             @RequestParam(name = "keyword", required = false) String keyword,
                                                                             @RequestParam(name = "type", defaultValue = "ALL") HubSearchType type,
                                                                             @PageableDefault Pageable pageable,
                                                                             @RequestParam(name = "sortBy", defaultValue = "CREATEDAT") SortType sortBy,
                                                                             @RequestParam(name = "isAsc", defaultValue = "true") boolean isAsc) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_GET_HUBS, hubService.searchHubs(userId, role, keyword, type, pageable, sortBy, isAsc))
        );
    }

    @GetMapping("/company-address")
    public ResponseEntity<SuccessResponse<HubResponseDto>> getHubFromCompanyAddress(
            @RequestHeader(value = "X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Role") String role,
            @RequestParam(name = "address") String address,
            @RequestParam(name = "lat") double lat,
            @RequestParam(name = "lng") double lng
           ) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_GET_HUB, hubService.getHubFromCompanyAddress(userId, role, address, lat, lng))
        );
    }

    @PostMapping("/list")
    public ResponseEntity<SuccessResponse<List<HubResponseDto>>> getHubsFromList(@RequestBody List<UUID> idList) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_GET_HUBS, hubService.getHubListFromIdList(idList))
        );
    }

}
