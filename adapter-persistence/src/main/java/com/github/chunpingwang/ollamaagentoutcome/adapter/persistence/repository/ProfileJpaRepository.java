package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;

/**
 * Profile JPA Repository。
 */
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID> {

    /**
     * 檢查指定 Email 是否存在。
     */
    boolean existsByEmail(String email);

    /**
     * 檢查指定 Email 是否存在且 ID 不同。
     */
    boolean existsByEmailAndIdNot(String email, UUID id);
}
