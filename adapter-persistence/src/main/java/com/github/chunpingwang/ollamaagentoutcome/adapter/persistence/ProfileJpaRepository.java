package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 個人資料的 Spring Data Repository。
 */
public interface ProfileJpaRepository extends JpaRepository<ProfileJpaEntity, String> {
}
