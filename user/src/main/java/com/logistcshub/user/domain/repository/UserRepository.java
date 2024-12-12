package com.logistcshub.user.domain.repository;

import com.logistcshub.user.domain.model.User;
import com.logistcshub.user.infrastructure.repository.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> , UserRepositoryCustom {
    Optional<User> findByEmail(String email);
}
