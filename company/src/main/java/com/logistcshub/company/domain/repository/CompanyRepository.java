package com.logistcshub.company.domain.repository;

import com.logistcshub.company.domain.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID>, CompanyRepositoryCustom {
}
