package com.logistcshub.user.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {
    @GetMapping("/api/hubs/{id}")
    ResponseEntity<?> getHub(@RequestHeader(value = "X-USER-ID") Long userId,
                             @RequestHeader(value = "X-USER-ROLE") String role,
                             @PathVariable UUID id);

}

