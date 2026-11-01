package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 個人資料的 Spring Data Repository。
 */
@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    /**
     * 檢查指定電子郵件是否已存在。
     */
    boolean existsByEmail(String email);
}
