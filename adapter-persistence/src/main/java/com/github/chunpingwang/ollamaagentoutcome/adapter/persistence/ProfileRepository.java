package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 個人資料 Spring Data Repository。
 */
public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    /**
     * 根據電子郵件檢查是否存在。
     *
     * @param email 電子郵件地址
     * @return 若存在則回傳 true
     */
    boolean existsByEmail(String email);
}
