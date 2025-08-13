package com.repository;

import com.model.CompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyUserRepository extends JpaRepository<CompanyUser, Long> {
    Optional<CompanyUser> findByLogin(String login);
}