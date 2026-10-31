package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA Repository 用於 ProfileEntity 的資料庫存取操作。
 */
interface ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID> {

    Optional<ProfileEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
