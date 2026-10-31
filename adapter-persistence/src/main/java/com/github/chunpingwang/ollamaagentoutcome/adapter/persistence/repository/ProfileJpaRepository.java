package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;

/**
 * Spring Data JPA Repository for ProfileEntity using UUID primary key.
 */
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID> {

    Optional<ProfileEntity> findByEmail(String email);
}
