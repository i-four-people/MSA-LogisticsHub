package com.logistcshub.user.infrastructure.repository;

import com.logistcshub.user.domain.model.user.User;
import com.logistcshub.user.domain.repository.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByEmail(String email);
}
