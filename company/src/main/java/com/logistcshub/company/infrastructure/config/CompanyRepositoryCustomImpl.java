package com.logistcshub.company.infrastructure.config;

import com.logistcshub.company.domain.model.Company;
import com.logistcshub.company.domain.model.CompanyType;
import com.logistcshub.company.domain.model.QCompany;
import com.logistcshub.company.domain.repository.CompanyRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CompanyRepositoryCustomImpl implements CompanyRepositoryCustom {

    @Autowired
    private JPAQueryFactory queryFactory;


    @Override
    public Page<Company> getCompanies(UUID id,
                                      String name,
                                      String companyType,
                                      String address,
                                      String contact,
                                      String sortBy,
                                      boolean isAsc,
                                      Pageable pageable) {
        QCompany company = QCompany.company; BooleanBuilder builder = new BooleanBuilder();
        if (id != null) {
            builder.and(company.id.eq(id)); } if (name != null) { builder.and(company.name.containsIgnoreCase(name));
        }
        if (companyType != null) {
            builder.and(company.companyType.eq(CompanyType.valueOf(companyType))); }
        if (address != null) {
            builder.and(company.address.containsIgnoreCase(address));
        }
        if (contact != null) {
            builder.and(company.contact.containsIgnoreCase(contact));
        }
        List<Company> companies = queryFactory.selectFrom(company)
                .where(builder)
                .orderBy(isAsc ? company.createdAt.asc() : company.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(companies, pageable, queryFactory.selectFrom(company).where(builder).fetchCount()); }
}
