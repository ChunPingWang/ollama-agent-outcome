package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA Repository for ProfileEntity.
 */
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, java.util.UUID>, JpaSpecificationExecutor<ProfileEntity> {
}
