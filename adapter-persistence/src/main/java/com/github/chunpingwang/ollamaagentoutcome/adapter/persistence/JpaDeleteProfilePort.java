package com.github.chunpingwang.ollamaagentoutcome.adapter.persistence;


import com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

/**
 * Implementation of DeleteProfilePort using JPA.
 */
@Repository
public class JpaDeleteProfilePort implements com.github.chunpingwang.ollamaagentoutcome.application.port.out.DeleteProfilePort {

    private final ProfileRepository profileRepository;

    public JpaDeleteProfilePort(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public void deleteById(String id) {
        // Check existence first to ensure proper error handling if needed by service layer, 
        // though the port itself just deletes. The Service layer handles NotFound logic usually.
        // However, for simplicity and consistency with other ports, we can let repository handle it or check.
        // Given the spec says "delete", we proceed directly. If not found, nothing happens in DB.
        // But typically services want to know if it existed. Let's assume the Port is low-level.
        // To match typical patterns where Service checks existence:
        var existing = profileRepository.findById(id);
        if (existing.isPresent()) {
            profileRepository.deleteById(id);
        } else {
             throw new com.github.chunpingwang.ollamaagentoutcome.domain.ProfileNotFoundException(id);
        }
    }
}
