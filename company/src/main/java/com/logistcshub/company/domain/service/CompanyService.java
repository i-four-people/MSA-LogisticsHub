package com.logistcshub.company.domain.service;

import com.logistcshub.company.domain.model.Company;
import com.logistcshub.company.domain.repository.CompanyRepository;
import com.logistcshub.company.presentation.request.CompanyRequestDto;
import com.logistcshub.company.presentation.response.CompanyResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final JPAQueryFactory queryFactory;

    public CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto,Long id) {
        Company company = companyRequestDto.toEntity();
        company.create(id);
        companyRepository.save(company);

        return CompanyResponseDto.toDto(company);
    }

    public CompanyResponseDto updateCompany(CompanyRequestDto companyRequestDto, UUID id, Long userId) {
        Company company = companyRepository.findById(id).orElseThrow( () -> new NoSuchElementException("해당 Id값을 갖는 업체가 존재하지 않습니다."));
        company.update(userId.toString(), companyRequestDto);
        companyRepository.save(company);

        return CompanyResponseDto.toDto(company);
    }

    public CompanyResponseDto deleteCompany(UUID id, Long userId) {
        Company company = companyRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 Id값을 갖는 업체가 존재하지 않습니다."));
        company.delete(userId.toString());
        companyRepository.save(company);

        return CompanyResponseDto.toDto(company);
    }

    @Transactional
    public Page<CompanyResponseDto> getCompanies(UUID id,
                                                 String name,
                                                 String companyType,
                                                 String address,
                                                 String contact,
                                                 String sortBy,
                                                 boolean isAsc,
                                                 Pageable pageable) {
        return companyRepository.getCompanies(id, name, companyType, address, contact, sortBy, isAsc, pageable)
                .map(CompanyResponseDto::toDto);
    }
}
