package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;

/**
 * Spring Data JPA Repository：負責 Profile 的持久化操作。
 */
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID> {

    /**
     * 根據 ID 查詢個人資料。
     *
     * @param id 資料識別碼
     * @return 找到的 Entity，若不存在則回傳 Optional.empty()
     */
    Optional<ProfileEntity> findById(UUID id);
}
