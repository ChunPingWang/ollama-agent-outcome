package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 個人資料的 Spring Data JPA Repository。
 */
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID> {

    /**
     * 根據電子郵件查詢個人資料。
     *
     * @param email 電子郵件
     * @return 找到的 Entity，若無則為空
     */
    Optional<ProfileEntity> findByEmail(String email);
}
