package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA Repository for ProfileEntity.
 */
interface ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID> {
}
