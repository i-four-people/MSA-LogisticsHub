package com.logistcshub.company.presentation.controller;

import com.logistcshub.company.domain.service.CompanyService;
import com.logistcshub.company.presentation.request.CompanyRequestDto;
import com.logistcshub.company.presentation.response.ApiResponse;
import com.logistcshub.company.presentation.response.CompanyResponseDto;
import com.logistcshub.company.presentation.response.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponseDto>> createCompany(@RequestBody CompanyRequestDto companyRequestDto) {
//        if(role != "MASTER" || role != "HUB_MANAGER"){
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "업체 생성 권한이 없습니다.");
//        }
        Long userId = 111L;
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageType.CREATE, companyService.createCompany(companyRequestDto,userId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> updateCompany(@RequestBody CompanyRequestDto companyRequestDto,
                                                                         @PathVariable UUID id) {
        Long userId = 222L;
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(MessageType.UPDATE, companyService.updateCompany(companyRequestDto, id, userId)));
    }

}
