package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 個人資料的 Spring Data Repository。
 */
interface ProfileRepository extends JpaRepository<ProfileEntity, String> {

    /**
     * 根據電子郵件查詢資料。
     */
    Optional<ProfileEntity> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
