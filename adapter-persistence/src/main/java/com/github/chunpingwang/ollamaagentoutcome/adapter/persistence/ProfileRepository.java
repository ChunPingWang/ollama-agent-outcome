package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 個人資料的 Spring Data JPA Repository。
 */
interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    /**
     * 檢查指定電子郵件是否已存在。
     *
     * @param email 要檢查的電子郵件地址
     * @return 若存在則回傳 true，否則為 false
     */
    boolean existsByEmail(String email);
}
