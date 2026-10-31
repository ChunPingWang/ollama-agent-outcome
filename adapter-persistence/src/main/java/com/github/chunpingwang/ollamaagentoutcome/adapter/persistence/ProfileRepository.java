package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data Repository for ProfileEntity persistence operations.
 */
interface ProfileRepository extends JpaRepository<ProfileEntity, String> {

    Optional<ProfileEntity> findByEmail(String email);
    
    boolean existsByEmailAndIdNotIn(String email, Iterable<String> idsToExclude);
}
