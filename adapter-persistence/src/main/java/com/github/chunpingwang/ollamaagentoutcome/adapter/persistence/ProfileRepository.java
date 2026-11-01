package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data Repository for ProfileEntity persistence operations.
 */
public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {

    /**
     * Check if a profile exists with the given email address.
     */
    boolean existsByEmail(String email);

    /**
     * Check if a profile exists with the given email address excluding the specified ID.
     */
    boolean existsByEmailAndIdNot(String email, String id);
}
