package com.logistcshub.company.domain.repository;

import com.logistcshub.company.domain.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CompanyRepositoryCustom {
    Page<Company> getCompanies(UUID id,
                               String name,
                               String companyType,
                               String address,
                               String contact,
                               String sortBy,
                               boolean isAsc,
                               Pageable pageable);

}
