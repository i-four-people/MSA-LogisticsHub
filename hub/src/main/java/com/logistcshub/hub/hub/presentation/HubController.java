package com.logistcshub.hub.hub.presentation;

import static com.logistcshub.hub.common.domain.model.type.ResponseMessage.SUCCESS_CREATE_HUB;

import com.logistcshub.hub.common.domain.model.dtos.SuccessResponse;
import com.logistcshub.hub.hub.application.dtos.AddHubResponseDto;
import com.logistcshub.hub.hub.application.service.HubService;
import com.logistcshub.hub.hub.presentation.request.AddHubRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hubs")
@RequiredArgsConstructor
public class HubController {

    private final HubService hubService;

    @PostMapping
    public ResponseEntity<SuccessResponse<AddHubResponseDto>> addHub(@RequestBody AddHubRequestDto request,
                                                                        @RequestHeader(value = "X-USER-ID")UUID userId,
                                                                        @RequestHeader(value = "X-USER-ROLE") String role) {
        userId = UUID.fromString("1f40b195-4bcd-408c-8589-ed4567c5294e");
        role = "MASTER";

        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_CREATE_HUB, hubService.addHub(userId, role, request))
        );

    }

}
