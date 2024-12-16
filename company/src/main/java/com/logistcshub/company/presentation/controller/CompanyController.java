package com.logistcshub.company.presentation.controller;

import com.logistcshub.company.application.dto.CompanyResponse;
import com.logistcshub.company.domain.model.Company;
import com.logistcshub.company.domain.service.CompanyService;
import com.logistcshub.company.presentation.request.CompanyRequestDto;
import com.logistcshub.company.presentation.response.ApiResponse;
import com.logistcshub.company.presentation.response.CompanyResponseDto;
import com.logistcshub.company.presentation.response.MessageType;
import com.querydsl.core.types.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService companyService;

    // 업체 생성
    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponseDto>> createCompany(@RequestBody CompanyRequestDto companyRequestDto,
                                                                         @RequestHeader(value = "X-USER-ID") Long userId,
                                                                         @RequestHeader(value = "X-USER-ROLE") String role) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageType.CREATE, companyService.createCompany(userId, role, companyRequestDto)));
    }
//    업체 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> updateCompany(@RequestBody CompanyRequestDto companyRequestDto,
                                                                         @PathVariable UUID id,
                                                                         @RequestHeader(value = "X-USER-ID") Long userId,
                                                                         @RequestHeader(value = "X-USER-ROLE") String role) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(MessageType.UPDATE, companyService.updateCompany(userId, role, companyRequestDto, id)));
    }

//    업체 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> deleteCompany(@PathVariable UUID id,
                                                                         @RequestHeader(value = "X-USER-ID") Long userId,
                                                                         @RequestHeader(value = "X-USER-ROLE") String role) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(MessageType.DELETE, companyService.deleteCompany(id, userId, role)));
    }
//    업체 전체 조회 및 검색
    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<CompanyResponseDto>>> getCompanies(@QuerydslPredicate(root = Company.class) Predicate predicate,
                                                                                    @PageableDefault Pageable pageable) {
        PagedModel<CompanyResponseDto> Companies = companyService.getCompanies(predicate, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(MessageType.RETRIEVE, Companies));
    }

//    단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> findCompanyById(@PathVariable(value = "id") UUID id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(MessageType.RETRIEVE, companyService.getCompany(id)));
    }

    @PostMapping("/batch")
    List<CompanyResponse> findCompaniesByIds(@RequestBody List<UUID> ids){
        return companyService.findCompaniesByIds(ids);
    };

}
