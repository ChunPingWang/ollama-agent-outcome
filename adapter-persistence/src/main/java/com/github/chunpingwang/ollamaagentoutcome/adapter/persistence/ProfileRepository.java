package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Spring Data Repository for ProfileEntity.
 */
public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {
    
    Optional<ProfileEntity> findById(String id);
}
