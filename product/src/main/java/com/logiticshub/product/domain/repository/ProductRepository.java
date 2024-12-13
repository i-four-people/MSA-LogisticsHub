package com.logiticshub.product.domain.repository;

import com.logiticshub.product.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>,
        ProductRepositoryCustom,
        QuerydslPredicateExecutor<Product> {

}
