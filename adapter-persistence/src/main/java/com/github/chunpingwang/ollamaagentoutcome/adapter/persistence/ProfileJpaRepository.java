package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 個人資料的 Spring Data JPA Repository。
 */
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, String> {

    /**
     * 根據電子郵件查詢個人資料。
     *
     * @param email 電子郵件
     * @return 找到的個人資料
     */
    Optional<ProfileEntity> findByEmail(String email);

    /**
     * 檢查電子郵件是否存在。
     *
     * @param email 電子郵件
     * @return 是否存在
     */
    boolean existsByEmail(String email);
}
