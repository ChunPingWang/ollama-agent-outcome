package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 個人資料的 Spring Data JPA Repository。
 */
interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    /**
     * 根據電子郵件查找個人資料。
     *
     * @param email 電子郵件地址
     * @return 找到的 Entity，若不存在則為 Optional.empty()
     */
    Optional<ProfileEntity> findByEmail(String email);
}
