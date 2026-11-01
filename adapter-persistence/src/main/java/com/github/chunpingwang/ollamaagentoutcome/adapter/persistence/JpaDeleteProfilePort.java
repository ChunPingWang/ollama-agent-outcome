package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;



import org.springframework.stereotype.Component;import java.util.UUID;

import org.springframework.dao.DataAccessException;

import com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort;

/**
 * JPA implementation of DeleteProfilePort.
 */
@Component
public class JpaDeleteProfilePort implements DeleteProfilePort {

    private final ProfileJpaRepository repository;

    public JpaDeleteProfilePort(ProfileJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteById(UUID id) {
        // Repository.deleteById throws EmptyResultDataAccessException if not found, 
        // but typically the Service layer checks existence first to throw specific domain exceptions (e.g., ProfileNotFoundException).
        // Here we just delegate deletion.
        try {
            repository.deleteById(id);
        } catch (DataAccessException e) {
            // Re-throw or wrap as needed depending on strictness. For now, let it propagate or handle generic DB errors.
            throw new RuntimeException("Failed to delete profile", e);
        }
    }
}
