package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * 個人資料的 JPA Repository。
 */
public interface JpaProfileRepository extends JpaRepository<ProfileEntity, UUID> {
}
