package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;

import java.util.UUID;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfileByIdPort;

/**
 * Port implementation to delete a profile by ID via JPA.
 */
@Service
class JpaDeleteProfileByIdPort implements DeleteProfileByIdPort {

    private final ProfileJpaRepository repository;

    public JpaDeleteProfileByIdPort(ProfileJpaRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public void deleteById(UUID id) {
        // Optional: Check existence first if required by business logic, 
        // but typically DB constraint or Use Case handles it.
        // Here we just delegate to repo. If not found, nothing happens unless cascade/delete-orphan is set.
        // To match typical behavior where deleting non-existent might be ignored or throw, 
        // let's assume standard JPA behavior (no-op for missing entity).
        repository.deleteById(id);
    }
}
