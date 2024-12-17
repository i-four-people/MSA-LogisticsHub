package com.logistcshub.company.domain.repository;

import com.logistcshub.company.domain.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID>,
        CompanyRepositoryCustom ,
        QuerydslPredicateExecutor<Company> {
    Company findByName(String companyName);
}
