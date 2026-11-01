package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data Repository：個人資料持久化操作。
 */
public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {
}
