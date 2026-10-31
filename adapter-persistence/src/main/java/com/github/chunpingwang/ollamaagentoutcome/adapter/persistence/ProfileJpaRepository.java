package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA Repository for ProfileEntity.
 */
interface ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID> {

    /**
     * Checks if a profile exists with the given email address.
     * @param email the email to check
     * @return true if an entity with that email exists
     */
    boolean existsByEmail(String email);
}
