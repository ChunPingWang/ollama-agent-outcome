package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;

/**
 * Spring Data JPA Repository：負責 Profile 的持久化操作。
 */
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID> {
}
