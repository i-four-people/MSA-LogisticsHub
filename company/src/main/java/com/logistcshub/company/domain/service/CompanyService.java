package com.logistcshub.company.domain.service;

import com.logistcshub.company.domain.model.Company;
import com.logistcshub.company.domain.repository.CompanyRepository;
import com.logistcshub.company.presentation.request.CompanyRequestDto;
import com.logistcshub.company.presentation.response.CompanyResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto,Long id) {
        Company company = companyRequestDto.toEntity();
        company.create(id);
        companyRepository.save(company);

        return CompanyResponseDto.toDto(company);
    }
}
