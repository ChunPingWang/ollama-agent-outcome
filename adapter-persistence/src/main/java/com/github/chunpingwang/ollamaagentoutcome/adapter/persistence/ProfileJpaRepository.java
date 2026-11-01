package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 個人資料的 Spring Data JPA Repository。
 */
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, String> {

    Optional<ProfileEntity> findById(String id);

    boolean existsByEmail(String email);
}
