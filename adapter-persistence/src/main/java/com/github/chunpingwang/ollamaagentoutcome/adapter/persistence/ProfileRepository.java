package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data Repository，提供個人資料的資料庫存取能力。
 */
interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    boolean existsByEmail(String email);
}
