package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for ProfileEntity.
 */
public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {
}
