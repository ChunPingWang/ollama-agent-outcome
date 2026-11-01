package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for ProfileEntity.
 */
@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {
}
