package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 個人資料的 Spring Data JPA Repository。
 */
interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {
}
