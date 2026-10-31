package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.entity.ProfileEntity;

/**
 * Spring Data Repository for ProfileEntity.
 */
public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {

    Optional<ProfileEntity> findByEmail(String email);
    
    boolean existsByEmailAndIdNot(String email, String id);
}
