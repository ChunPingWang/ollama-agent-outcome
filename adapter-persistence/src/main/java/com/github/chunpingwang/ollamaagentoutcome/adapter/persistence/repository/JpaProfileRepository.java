package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.chunpingwang.ollamaagentoutcome.adapter.persistence.profile.ProfileEntity;

/**
 * Spring Data JPA Repository for Profile Entity.
 */
public interface JpaProfileRepository extends JpaRepository<ProfileEntity, String> {

    /**
     * Find a profile by its email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the entity if found, empty otherwise
     */
    Optional<ProfileEntity> findByEmail(String email);
}
