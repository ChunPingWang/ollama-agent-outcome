package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

/**
 * 個人資料的 Spring Data Repository。
 */
public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    /**
     * 根據電子郵件檢查是否存在記錄。
     */
    boolean existsByEmail(String email);

    /**
     * 根據電子郵件查詢記錄。
     */
    Optional<ProfileEntity> findByEmail(String email);
}
