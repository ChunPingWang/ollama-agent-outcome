package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA Repository，負責 Profile 的持久化操作。
 */
public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    /**
     * 根據電子郵件查詢個人資料。
     *
     * @param email 電子郵件地址
     * @return 找到的 Entity Optional
     */
    Optional<ProfileEntity> findByEmail(String email);

    /**
     * 檢查指定電子郵件是否已存在。
     *
     * @param email 電子郵件地址
     * @return 若存在則回傳 true
     */
    boolean existsByEmail(String email);
}
