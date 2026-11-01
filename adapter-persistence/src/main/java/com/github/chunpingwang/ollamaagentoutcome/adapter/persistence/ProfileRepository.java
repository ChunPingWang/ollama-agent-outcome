package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 個人資料的 Spring Data Repository。
 */
public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    /**
     * 檢查指定電子郵件是否已存在。
     */
    boolean existsByEmail(String email);
}
